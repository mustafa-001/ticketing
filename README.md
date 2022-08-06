##  Uygulamanın Parçalarının Genel Açıklaması

### `ticketing` Uygulaması
    
Kurumsal ve bireysel kullanıcıların kullanacakları endpointleri içeren uygulamadır. Sisteme kayıtlı
bir kullanıcı olmasını gerektiren endpointler aldıkları parametrelerde `userId` isimli, sistemde kayıtlı
kullanıcıları referans gösteren bir `Long` parametrenin olmasını gerektirir. 

- `/users ` endpointi kullanıcıların kayıt olması, kullanıcı e-postası ve şifresini değiştirmesi, kullanıcı detaylarını 
sorgulaması ve kullanıcıyı silmesi işlevlerini yapar.
- `/tickets` endpointi tek bir bilet alınması, çoklu alınması, kullanıcıların aldığı biletleri listeleyebilmesi ve
mevcut seferler içinden arama yapması işlevlerini yapar.

#### Domainde kullanılan modeller ve veritabanı:
- `User` bir kullanıcıyı temsil eder.
- `Ticket` satılmış bir bileti temsil eder.
- ticketing adlı database kullanılır ve bunu `ticketing-admin` uygulaması ile paylaşır.
 
 
   Gereksinimlerde Spring Security gibi bir zorunluluk olmadığı için her endpoint ayrı ayrı her kullanışta
kullanıcının login detaylarını sormaz yada bir session token, jwt vb. istemez. Şu aşamada kullanıcının sadece
kendisi için işlem yapacağını varsayar. (`login`, `changePassword` ve `changeEmail` endpointleri hariç.)

### `ticketing-admin` Servisi
Yönetici(admin) yetkisine sahip kullanıcıların kullancakları endpointleri içeren uygulamadır. 

- `/adminUsers` endpointi yönetici kullanıcıların kayıt olması, kullanıcı e-postası ve şifresini değiştirmesi, 
kullanıcı detaylarını sorgulaması ve kullanıcıyı silmesi işlevlerini yapar.
- `/trips` endpointi yöneticilerin yeni sefer eklemesi, iptal etmesi, toplam bilet satışı ve elde edilen ücreti
sorgulamasını sağlar.

####  

- `AdminUser` bir yönetici kullanıcıyı temsil eder. Amaçları farklı olduğu için `User` entity'si
ile polimorfik bir ilişkisi yoktur ve farklı bir tabloda saklanır. `User`'ın amacı bilet satın almak, `AdminUser`ın amacı
seferleri kontrol etmektir. Aralarında herhangi bir is-as yada has-is ilişkisi yoktur.
- `Trip` detayları sisteme girilmiş bir seferi temsil eder, yönetici yetkisine sahip kullanıcılar tarafından girilir/düzenlenir,
bireysel/kurumsal kullanıcılar tarafında okunur.

- ticketing adlı database kullanılır ve bunu `ticketing` uygulaması ile paylaşır.

### `ticketing-payment` Servisi

Kullanıcılar bir bilet satın aldığında ödeme işlemini simüle eden uygulamadır. `FeignClient` ile çalışır, gelen isteği
veritabanına kaydeder ve ödemenin gerçekleştiğini işaret eden cevap döndürür.

#### Domainde kullanılan modeller ve veritabanı:
- `Payment` bir gerçekleştirilen bir ödemeyi temsil eder.
- ticketing-payment adlı database kullanılır.

### `ticketing-emailandsms` Servisi

Kullanıcılar sisteme kaydolduklarında e-posta, bilet aldıklarında da SMS göndermeyi simüle eden servistir.
`RabbitMQ` ile çalışır. 

#### Domainde kullanılan modeller ve veritabanı:
- `Message` gönderilen her e-posta yada SMS'i temsil eden ana abstract sınıftır.
- `Email` ve `SMS`, `Message` sınıfından kalıtım alırlar.
- ticketing-message adlı MongoDB veritabanı kullanılır.



## Sistemin Gereksinimleri ve Sistemi Çalıştırma

### Gereksinimler
Sistemden `docker` ile de karşılanabilecek 3 teknolojiye gereksinim duyar.
`docker-compose.yml` dosyasında bunların hepsi tanımlıdır ve `docker compose up` ile çalıştırılabilir.

- PostgreSQL (localhost:5432 portunda çalışan)
- MongoDB (localhost:27017 portunda çalışan)
- RabbitMQ(localhost:5672 portunda çalışan)
- PostgreSQL (localhost:5433 portunda çalışan)

### Çalıştırma
Daha sonra `ticketing`, `ticketing-admin`, `ticketing-payment` ve `ticketing-emailandsms` dizinlerinde `./mvnw spring-boot:run`
ile servisler sırasıla 8080, 8081, 8082 ve 8083 portlarında ayağa kaldırılabilir.

### Test Etme Ve Kullanma
- Ana dizindeki ticketing.postman_collection.json dosyasını Postman'e aktararılabilinir.
- Bu dizindeki endpoint-açıklamalar.md dosyasından hepsinin listesine erişilebilinir.

## Kullanılan Teknolojiler
### `ticketing`
- Spring Data JPA
- OpenFeign - `ticketing-payment` servisi ile iletişim kurmak için.
- RabbitMQ - `ticketing-emailandsms` servisi ile iletişim kurmak için.
 
### `ticketing-admin`
- Spring Data JPA
 
### `ticketing-payment`
- Spring JDBC

### `ticketing-emailandsms`
- Spring Data MongoDB
- RabbitMQ

