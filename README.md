# API Management Platform

## Proje Hakkında

API Management Platform, şirketlerin kendi API'larını dış dünyaya güvenli bir şekilde açmasını sağlayan bir sistemdir. 

Diyelim ki bir fintech şirketi var ve Trendyol, Yemeksepeti, Migros gibi onlarca farklı partner bu şirketin API'sini kullanmak istiyor. Her partner için ayrı ayrı güvenlik ayarı yapmak, kim ne kadar istek attı takip etmek, limitler koymak, ay sonunda kullanıma göre fatura kesmek gerekiyor. İşte bu platform tüm bu işleri tek bir yerden yönetmeyi sağlıyor.

## Ne İşe Yarıyor?

Bir partner sisteme API key alarak dahil oluyor. Bu key ile istek attığında platform şunları yapıyor:

Önce gelen isteğin geçerli bir API key'e sahip olup olmadığını kontrol ediyor. Sonra bu partnerin belirlenen limitleri aşıp aşmadığına bakıyor. Örneğin dakikada 1000 istek hakkı varsa ve 1001. isteği atıyorsa reddediyor. Eğer her şey yolundaysa isteği asıl servise yönlendiriyor. Bu sırada her isteği kaydediyor ki ay sonunda "Trendyol bu ay 2 milyon istek attı" diye rapor çıkabilsin. Partner limitinin %80'ine yaklaştığında otomatik email atıyor. Ay sonunda kullanıma göre fatura oluşturuyor.

## Teknik Yapı

Sistem 6 farklı servisten oluşuyor ve her biri bağımsız çalışıyor.

API Gateway tüm isteklerin ilk geldiği yer. Burada kimlik doğrulama, rate limiting ve yönlendirme yapılıyor. Redis kullanarak her API key için anlık istek sayısı tutuluyor.

User Service kullanıcı kayıt, giriş ve yetkilendirme işlemlerini yapıyor. JWT token üretiyor. Google ve GitHub ile giriş yapılabiliyor.

Organization Service şirketleri ve API key'leri yönetiyor. Bir şirket sisteme kayıt olduğunda buradan API key alıyor. Her şirketin verileri birbirinden tamamen izole tutuluyor.

Analytics Service tüm API çağrılarını kaydediyor ve raporluyor. Kafka üzerinden gelen eventleri dinliyor ve MongoDB'ye yazıyor. Günlük, haftalık, aylık kullanım raporları çıkarıyor.

Notification Service limit uyarıları, sistem bildirimleri gibi mesajları email, SMS veya webhook olarak gönderiyor.

Billing Service kullanım miktarına göre fatura hesaplıyor ve ödeme entegrasyonu sağlıyor.

## Servisler Nasıl Haberleşiyor?

İki türlü iletişim var. 

Senkron iletişimde bir servis diğerini çağırıyor ve cevap bekliyor. Örneğin API Gateway, User Service'e "bu token geçerli mi" diye soruyor ve cevabı bekliyor. Bu REST API ile yapılıyor.

Asenkron iletişimde bir servis mesaj bırakıyor ve cevap beklemiyor. Örneğin bir API çağrısı geldiğinde Gateway bunu Kafka'ya yazıyor. Analytics Service müsait olduğunda bu mesajı alıp işliyor. Bu sayede Gateway yavaşlamıyor.

## Veritabanları

PostgreSQL kullanıcılar, şirketler, API key'ler ve faturalar gibi ilişkisel verileri tutuyor.

MongoDB API çağrı logları ve analytics verileri gibi yapısı esnek olan verileri tutuyor.

Redis önbellekleme ve rate limiting için kullanılıyor. API key bilgileri burada cache'leniyor ki her istekte veritabanına gidilmesin. Ayrıca her API key için anlık istek sayacı burada tutuluyor.

## Rate Limiting Nasıl Çalışıyor?

Sliding window algoritması kullanılıyor. Sabit pencere yönteminde örneğin 00:00-01:00 arası 1000 istek hakkı var, 01:00'da sıfırlanıyor. Ama bu durumda biri 00:59'da 1000, 01:01'de 1000 istek atarsa 2 dakikada 2000 istek atmış oluyor.

Sliding window'da ise her an son 1 dakikaya bakılıyor. Şu an 01:30 ise 00:30-01:30 arasındaki istekler sayılıyor. Bu daha adil bir yöntem.

## Multi-tenant Ne Demek?

Aynı sistem üzerinde birden fazla şirket çalışıyor ama birbirlerinin verilerini göremiyor. Trendyol kendi kullanıcılarını görüyor, Yemeksepeti kendi kullanıcılarını görüyor. Her veritabanı sorgusuna organizasyon ID'si ekleniyor ve böylece veri izolasyonu sağlanıyor.

## Deployment

Tüm servisler Docker container olarak paketleniyor. Kubernetes üzerinde çalışıyor. Helm chart'lar ile deployment yapılıyor ki farklı ortamlar için (development, staging, production) ayarlar kolayca değiştirilebilsin.

Her servis için otomatik ölçeklendirme ayarlanmış durumda. CPU kullanımı %70'i geçerse yeni pod'lar ekleniyor.

## CI/CD

Kod GitHub'a push edildiğinde otomatik olarak testler çalışıyor, kod kalitesi kontrol ediliyor, güvenlik taraması yapılıyor, Docker image oluşturuluyor ve Kubernetes'e deploy ediliyor. Hem Jenkins hem de GitHub Actions ile pipeline kurulu.

## Monitoring

Prometheus tüm servislerden metrik topluyor. Grafana'da dashboard'lar var: kaç istek geliyor, hata oranı ne, hangi servis yavaş, CPU ve bellek kullanımı nasıl.

ELK Stack ile tüm loglar merkezi bir yerde toplanıyor ve aranabilir hale geliyor.

Zipkin ile bir isteğin hangi servislerden geçtiği ve her birinde ne kadar süre harcandığı görülebiliyor.

## Kullanılan Teknolojiler

Backend tarafında Spring Boot 3, Spring Cloud, Spring Security, Spring Data JPA ve Spring Data MongoDB kullanılıyor.

Veritabanı olarak PostgreSQL, MongoDB ve Redis var.

Mesajlaşma için Apache Kafka ve RabbitMQ kullanılıyor.

DevOps tarafında Docker, Kubernetes, Helm, Jenkins ve GitHub Actions var.

Monitoring için Prometheus, Grafana, Elasticsearch, Logstash, Kibana ve Zipkin kullanılıyor.
