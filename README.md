- [Uygulamanın Parçalarının Genel Açıklaması](#uygulamanın-parçalarının-genel-açıklaması)
  - [`ticketing` Uygulaması](#ticketing-uygulaması)
    - [Domainde kullanılan modeller ve veritabanı:](#domainde-kullanılan-modeller-ve-veritabanı)
  - [`ticketing-admin` Servisi](#ticketing-admin-servisi)
    - [Domainde kullanılan modeller ve veritabanı:](#domainde-kullanılan-modeller-ve-veritabanı-1)
  - [`ticketing-payment` Servisi](#ticketing-payment-servisi)
    - [Domainde kullanılan modeller ve veritabanı:](#domainde-kullanılan-modeller-ve-veritabanı-2)
  - [`ticketing-emailandsms` Servisi](#ticketing-emailandsms-servisi)
    - [Domainde kullanılan modeller ve veritabanı:](#domainde-kullanılan-modeller-ve-veritabanı-3)
- [Sistemin Gereksinimleri ve Sistemi Çalıştırma](#sistemin-gereksinimleri-ve-sistemi-çalıştırma)
  - [Gereken Teknolojiler](#gereken-teknolojiler)
  - [Çalıştırma](#çalıştırma)
  - [Test Etme Ve Kullanma](#test-etme-ve-kullanma)
- [Sistem Kabul ve Gereksinimleri](#sistem-kabul-ve-gereksinimleri)
- [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
  - [`ticketing`](#ticketing)
  - [`ticketing-admin`](#ticketing-admin)
  - [`ticketing-payment`](#ticketing-payment)
  - [`ticketing-emailandsms`](#ticketing-emailandsms)
- [Sisteme Eklenebilecek Özellikler](#sisteme-eklenebilecek-özellikler)
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

#### Domainde kullanılan modeller ve veritabanı:

- `AdminUser` bir yönetici kullanıcıyı temsil eder. Amaçları farklı olduğu için `User` entity'si
ile polimorfik bir ilişkisi yoktur ve farklı bir tabloda saklanır. `User`'ın amacı bilet satın almak, `AdminUser`ın amacı
seferleri kontrol etmektir. Aralarında herhangi bir is-a yada has-a ilişkisi yoktur.
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
### Gereken Teknolojiler
Sistemden `docker` ile de karşılanabilecek 3 teknolojiye gereksinim duyar.
`docker-compose.yml` dosyasında bunların hepsi tanımlıdır ve `docker compose up` ile çalıştırılabilir.

- PostgreSQL (localhost:5432 portunda çalışan)
- MongoDB (localhost:27017 portunda çalışan)
- RabbitMQ(localhost:5672 portunda çalışan)
- PostgreSQL (localhost:5433 portunda çalışan)

### Çalıştırma
Daha sonra `ticketing`, `ticketing-admin`, `ticketing-payment` ve `ticketing-emailandsms` dizinlerinde `./mvnw spring-boot:run`
ile servisler sırasıyla 8080, 8081, 8082 ve 8083 portlarında ayağa kaldırılabilir.

### Test Etme Ve Kullanma
- Ana dizindeki ticketing.postman_collection.json dosyasını Postman'e aktarılabilir.

## Sistem Kabul ve Gereksinimleri

- [x] Kullanıcılar sisteme kayıt ve login olabilmelidir.
   + [UserService#create](ticketing/src/main/java/mutlu/ticketingapp/service/UserService.java#L43)
   + [UserService#login](ticketing/src/main/java/mutlu/ticketingapp/service/UserService.java#L99)

- [x] Kullanıcı kayıt işleminden sonra mail gönderilmelidir.
   + [UserService#create](ticketing/src/main/java/mutlu/ticketingapp/service/UserService.java#L60)

- [x] Kullanıcı şifresi istediğiniz bir hashing algoritmasıyla database kaydedilmelidir.
   + [UserService#create](ticketing/src/main/java/mutlu/ticketingapp/service/UserService.java#L57)

- [x] Admin kullanıcı yeni sefer ekleyebilir, iptal edebilir, toplam bilet satışını, bu satıştan elde edilen toplam ücreti görebilir.
   + [TripController#add](ticketing-admin/src/main/java/mutlu/ticketing_admin/controller/TripController.java#L24)
   + [TripController#delete](ticketing-admin/src/main/java/mutlu/ticketing_admin/controller/TripController.java#L42)
   + [TripController#getSoldTickets](ticketing-admin/src/main/java/mutlu/ticketing_admin/controller/TripController.java#L32)
   + [TripController#getRevenueFromPayment](ticketing-admin/src/main/java/mutlu/ticketing_admin/controller/TripController.java#L37)

- [x] Kullanıcılar şehir bilgisi, taşıt türü(uçak & otobüs) veya tarih bilgisi ile tüm seferleri arayabilmelidir.
   + [TicketController#search](ticketing/src/main/java/mutlu/ticketingapp/controller/TicketController.java#L40)

- [x] Bireysel kullanıcı aynı sefer için en fazla 5 bilet alabilir.
   + [TicketService#checkMaximumTicketLimits](ticketing/src/main/java/mutlu/ticketingapp/service/TicketService.java#L96)

- [x] Bireysel kullanıcı tek bir siparişte en fazla 2 erkek yolcu için bilet alabilir.
   + [TicketService#addBulk](ticketing/src/main/java/mutlu/ticketingapp/service/TicketService.java#L140)

- [x] Kurumsal kullanıcı aynı sefer için en fazla 20 bilet alabilir.
   + [TicketService#checkMaximumTicketLimits](ticketing/src/main/java/mutlu/ticketingapp/service/TicketService.java#L96)

- [x] Satın alma işlemi başarılı ise işlem tamamlanmalı ve asenkron olarak bilet detayları kullanıcının telefona numarasına mesaj gönderilmeli.
   + [TicketService#add](ticketing/src/main/java/mutlu/ticketingapp/service/TicketService.java#L79)

- [x] Mesaj ve mail gönderme işlemleri için sadece Database kayıt etme işlemi yapması yeterlidir. Fakat bu işlemler tek bir Servis(uygulama) üzerinden ve polimorfik davranış ile yapılmalıdır.
   + [Poliformik abstract sınıf olarak görev yapan Message sınıfı](ticketing-ticketing-emailandsms/src/main/java/com/mutlu/ticketingemailandsms/entity/Message.java)
   + [Kullanıldığı MessageListener sınıfı](ticketing-emailandsms/src/main/java/com/mutlu/ticketingemailandsms/listener/MessageListener.java)

- [x] Kullancılar  aldığı biletleri görebilmelidir.
   + [TicketController#getByUserId](ticketing/src/main/java/mutlu/ticketingapp/controller/TicketController.java#L36)

- [x] Kullanıcılar bireysel ve kurumsal olabilir.
   + [UserType enumı](ticketing/src/main/java/mutlu/ticketingapp/enums/UserType.java)

- [x] Uçak yolcu kapasitesi: 189
   + [VehicleType enumı ve kapasitenin uygulanması](ticketing/src/main/java/mutlu/ticketingapp/enums/VehicleType.java)

- [x] Otobüs yolcu kapasitesi: 45
   + [VehicleType enumı ve kapasitenin uygulanması](ticketing/src/main/java/mutlu/ticketingapp/enums/VehicleType.java)

- [x] Ödeme şekli sadece Kredi kartı ve Havale / EFT olabilir.
   + [PaymentType enumı](ticketing/src/main/java/mutlu/ticketingapp/enums/PaymentType)

- [x] Mesaj ve Mail gönderim işlemleri Asenkron olmalıdır.
   + [Ana uygulamada RabbitMQ](ticketing/src/main/java/mutlu/ticketingapp/config/RabbitMQConfig.java)

- [x] Ödeme Servisi işlemleri Senkron olmalıdır. 
   + [Ana uygulamada FeignClient olarak çalışan PaymentClient](ticketing/src/main/java/mutlu/ticketingapp/config/PaymentClient.java)
   + [Ödeme servisi olarak senkron çalışan ticketing-payment servisi](ticketing-payment/)

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

## Sisteme Eklenebilecek Özellikler
- Spring Security ile kullanıcı session yönetimi yapılabilir. Proje dahilinde olmadığı için özellikle yapılmadı.
- KUllanıcılara bilet iptal etme yetkisi verilebilir, bilet iptal edildiğinde ödeme servisi veritabanında ücret iadesi olmalıdır.
- Bilet satın alınırken koltuk numarası da dikkate alıabilir.
- `ticketing` ve `ticketing-admin` arasında ortak olan sınıflar common isimli bir kütüphaneye alınabilir. Bu kodun çoklanmasını engeller. Sistem bu ödev aşamasında fazla komplike olmadığı için bunu uygulamak gereksiz bir overhead yaratabilir.
