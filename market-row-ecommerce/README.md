# Market Row — Spring Boot E-Commerce Application ( JAVA 21/ WAR / Tomcat 9)

A full-stack e-commerce site built with **Java 21**, **Spring Boot 2.7.18**, **Spring MVC**,
**Spring Data JPA**, **Thymeleaf**, and an embedded **H2** file database (no external DB server
required). Packaged as a **WAR** for deployment into a standalone **Tomcat 9**.

> **Why Spring Boot 2.7 and not 3.x?** Spring Boot 3.x is built on the Jakarta EE
> `jakarta.servlet.*` namespace, which requires Tomcat 10.1+ — Tomcat 9 cannot load it (it deploys
> "successfully" with no errors, but nothing actually gets wired up, so every URL 404s). Spring Boot
> 2.7.18 is the last release line built on the older `javax.servlet.*` namespace that Tomcat 9
> understands, and it runs fine on Java 21 even though its official support window predates it.

Ships with 6 categories, ~30 seeded products (with images and price tags), a session-based
shopping cart, and a full checkout flow that produces an order confirmation.

## What's included

- Category browsing (Electronics, Fashion, Home & Kitchen, Books, Sports & Outdoors, Beauty)
- Product listing with images, prices, and stock status
- Product detail page with quantity selector
- Session-scoped shopping cart (add / update quantity / remove)
- Checkout form with server-side validation (name, email, phone, address)
- Order confirmation page with a generated order number
- Search bar across product names
- Self-seeding database — starts with demo data automatically, no manual SQL needed
- Responsive, self-contained CSS (no external frameworks required)

## Project structure

```
market-row-ecommerce/
├── pom.xml
├── src/main/java/com/marketrow/ecommerce/
│   ├── MarketRowApplication.java
│   ├── model/          (Category, Product, Order, OrderItem — JPA entities)
│   ├── cart/            (Cart, CartItem — session-scoped shopping cart)
│   ├── repository/      (Spring Data JPA repositories)
│   ├── service/         (ProductService, OrderService)
│   ├── dto/              (CheckoutForm)
│   ├── controller/     (Home, Product, Cart, Checkout controllers)
│   └── config/           (DataSeeder — seeds demo categories & products)
└── src/main/resources/
    ├── application.properties
    ├── templates/         (Thymeleaf views)
    └── static/css/        (style.css)
```

## 1. Build locally

Requires **JDK 21** and **Maven 3.6+**.

```bash
mvn clean package
```

This produces `target/market-row-ecommerce.war`.

## 2. Deploy to Tomcat 9 on an EC2 instance

### Step 1 — Install a full JDK 21 and Tomcat 9

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk maven

java -version      # should show 21.x
javac -version     # should also show 21.x — if missing, the JDK install is incomplete
```

If you already have Tomcat 9 installed elsewhere (e.g. `/opt/tomcat`), you can skip installing it
again — just note its `webapps` folder path for Step 4.

### Step 2 — Transfer the project to EC2

```bash
scp -i your-key.pem -r market-row-ecommerce ubuntu@<EC2_PUBLIC_IP>:/home/ubuntu/
```

### Step 3 — Build the WAR on the server

```bash
ssh -i your-key.pem ubuntu@<EC2_PUBLIC_IP>
cd market-row-ecommerce
mvn clean package
```

This produces `target/market-row-ecommerce.war`.

### Step 4 — Deploy the WAR into Tomcat 9

```bash
sudo cp target/market-row-ecommerce.war /opt/tomcat/webapps/
sudo systemctl restart tomcat9    # or your Tomcat 9 service name, e.g. `tomcat`
```

Tomcat auto-deploys it. It'll be reachable at:
```
http://<EC2_PUBLIC_IP>:8080/market-row-ecommerce/
```
(context path matches the WAR filename). To serve it at the root path instead
(`http://<EC2_PUBLIC_IP>:8080/`), rename it to `ROOT.war` before copying:
```bash
cp target/market-row-ecommerce.war /tmp/ROOT.war
sudo cp /tmp/ROOT.war /opt/tomcat/webapps/
```

### Step 5 — Open the security group port

AWS Console → EC2 → Security Groups → your instance's SG → Inbound rules → add:
- Type: Custom TCP, Port: **8080**, Source: `0.0.0.0/0` (or restrict to your IP)

### Step 6 — Verify it actually started (don't skip this)

```bash
sudo tail -f /opt/tomcat/logs/localhost.$(date +%Y-%m-%d).log
```
This is the log that shows Spring context startup errors — `catalina.out` only shows the
deploy/undeploy lifecycle, and can look "successful" even if the app failed to initialize
underneath. If you see a clean `Root WebApplicationContext: initialization completed` message,
you're good.

**Note on the H2 database path:** `spring.datasource.url` is a relative path
(`./data/marketrowdb`), which under Tomcat resolves relative to Tomcat's working directory (e.g.
`/opt/tomcat`), not this project folder. That's fine as-is, but for a fixed location, change it to
an absolute path in `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:/home/ubuntu/marketrow-data/marketrowdb;AUTO_SERVER=TRUE
```

### Redeploying after code changes

```bash
sudo rm -rf /opt/tomcat/webapps/market-row-ecommerce /opt/tomcat/webapps/market-row-ecommerce.war
mvn clean package
sudo cp target/market-row-ecommerce.war /opt/tomcat/webapps/
```

## 3. Customizing

- **Swap product images**: edit `DataSeeder.java` — change the `img(...)` seed strings, or point
  `imageUrl` fields at your own S3-hosted image URLs.
- **Change prices/products/categories**: edit the `saveProduct(...)` calls in `DataSeeder.java`.
  Delete the `./data` folder to force re-seeding on next startup (only seeds when the categories
  table is empty).
- **Currency symbol**: templates currently show `₹` (INR) — search-and-replace in the `.html` files
  under `templates/` if you want `$`, `€`, etc.
- **Switch to a real database** (MySQL/Postgres/RDS): replace the H2 dependency in `pom.xml` with
  your driver, and update the `spring.datasource.*` properties in `application.properties`.

## Notes

- The H2 database file persists orders and catalog data between restarts (`./data/marketrowdb.mv.db`).
  Back it up before redeploying if you want to keep order history.
- The cart is stored in the HTTP session, so it clears when a user's session expires or the server
  restarts (existing orders are unaffected — they're already saved to the database at checkout).
