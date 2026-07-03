# Market Row — Spring Boot E-Commerce Application

A full-stack e-commerce site built with **Java 21**, **Spring Boot 3.3**, **Spring MVC**, **Spring Data JPA**,
**Thymeleaf**, and an embedded **H2** file database (no external DB server required). Ships with 6
categories, ~30 seeded products (with images and price tags), a session-based shopping cart, and a
full checkout flow that produces an order confirmation.

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

Requires **JDK 21** and **Maven 3.9+** (or use the included `mvnw` if you add the wrapper).

```bash
mvn clean package
```

This produces `target/market-row-ecommerce.jar`. Run it:

```bash
java -jar target/market-row-ecommerce.jar
```

Visit **http://localhost:8080**. The database file is created at `./data/marketrowdb.mv.db` on first run
and is seeded automatically with categories and products.

## 2. Deploy to an EC2 instance (Ubuntu)

### Step 1 — Install a full JDK 21 (not just a JRE)

This is the step that most often trips people up — installing only a JRE, or an incomplete package,
causes Maven "release version not supported" errors during compilation. Install the full JDK:

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk maven
java -version      # should show 21.x
javac -version     # should also show 21.x — if this is missing, the JDK didn't install correctly
mvn -version        # confirm Maven is using Java 21
```

If `javac -version` doesn't work, the JDK install is incomplete — reinstall with:
```bash
sudo apt install --reinstall openjdk-21-jdk
```

### Step 2 — Transfer the project to EC2

From your local machine:

```bash
scp -i your-key.pem -r market-row-ecommerce ubuntu@<EC2_PUBLIC_IP>:/home/ubuntu/
```

Or clone from a Git repo if you push this project there first.

### Step 3 — Build on the server

```bash
ssh -i your-key.pem ubuntu@<EC2_PUBLIC_IP>
cd market-row-ecommerce
mvn clean package
```

### Step 4 — Open the security group port

In the AWS Console → EC2 → Security Groups → your instance's SG → Inbound rules → add:
- Type: Custom TCP, Port: **8080**, Source: `0.0.0.0/0` (or restrict to your IP)

(Or run behind Nginx on port 80 — see optional step below.)

### Step 5 — Run it

Quick test run (stops when you close the SSH session):
```bash
java -jar target/market-row-ecommerce.jar
```

For a persistent deployment, create a **systemd service** so it survives reboots and SSH disconnects:

```bash
sudo tee /etc/systemd/system/marketrow.service > /dev/null <<'EOF'
[Unit]
Description=Market Row E-Commerce App
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/market-row-ecommerce
ExecStart=/usr/bin/java -jar /home/ubuntu/market-row-ecommerce/target/market-row-ecommerce.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable marketrow
sudo systemctl start marketrow
sudo systemctl status marketrow
```

Then visit `http://<EC2_PUBLIC_IP>:8080`.

Useful commands:
```bash
sudo systemctl restart marketrow     # restart after redeploying a new jar
sudo journalctl -u marketrow -f      # tail logs
```

### Step 6 (optional) — Put Nginx in front on port 80

```bash
sudo apt install -y nginx
sudo tee /etc/nginx/sites-available/marketrow > /dev/null <<'EOF'
server {
    listen 80;
    server_name _;
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
EOF
sudo ln -s /etc/nginx/sites-available/marketrow /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl restart nginx
```

Then open inbound port 80 in the security group instead of (or in addition to) 8080, and visit
`http://<EC2_PUBLIC_IP>` directly.

## 2b. Deploying as a WAR into an existing standalone Tomcat

This project is configured to build a **WAR** file (see `<packaging>war</packaging>` in `pom.xml` and
`ServletInitializer.java`), for teams who already run a standalone Tomcat and want to drop
applications into it the traditional way, instead of running the self-contained jar.

```bash
mvn clean package
```

This produces `target/market-row-ecommerce.war`.

1. **Install Tomcat** (if not already installed), e.g. Tomcat 10.1+ (required for Spring Boot 3.x,
   which uses the Jakarta EE namespace — Tomcat 9 and earlier will NOT work):
   ```bash
   sudo apt install -y tomcat10
   ```

2. **Copy the WAR into Tomcat's webapps folder:**
   ```bash
   sudo cp target/market-row-ecommerce.war /var/lib/tomcat10/webapps/
   ```
   Tomcat will auto-deploy it. By default it will be reachable at:
   ```
   http://<EC2_PUBLIC_IP>:8080/market-row-ecommerce/
   ```
   (the context path matches the WAR's filename).

   To serve it at the site root instead (`http://<EC2_PUBLIC_IP>:8080/`), rename the file to
   `ROOT.war` before copying:
   ```bash
   cp target/market-row-ecommerce.war /tmp/ROOT.war
   sudo cp /tmp/ROOT.war /var/lib/tomcat10/webapps/
   ```

3. **Restart Tomcat** and check the logs:
   ```bash
   sudo systemctl restart tomcat10
   sudo tail -f /var/log/tomcat10/catalina.out
   ```

4. **Open the security group port** for Tomcat's port (default 8080), same as described in Step 4
   of the jar deployment section above.

**Note on the H2 database path:** `spring.datasource.url` is set to a relative path
(`./data/marketrowdb`). Under Tomcat, "relative" means relative to Tomcat's working directory
(usually `/var/lib/tomcat10` or `$CATALINA_BASE`), not the project folder. That's fine — the DB
file will just get created there instead — but if you want a fixed location, change the URL in
`application.properties` to an absolute path, e.g.:
```properties
spring.datasource.url=jdbc:h2:file:/home/ubuntu/marketrow-data/marketrowdb;AUTO_SERVER=TRUE
```

**Which should you actually use — jar or WAR?** Unless you specifically need to run multiple
applications inside one shared Tomcat, the executable jar (Option A above) is simpler to operate:
no separate Tomcat install, no context-path quirks, one process to manage via systemd. The WAR
route exists here because you asked for it, and it's a legitimate choice if you're standardizing
on a shared Tomcat across several apps.

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
