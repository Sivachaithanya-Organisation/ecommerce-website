package com.marketrow.ecommerce.config;

import com.marketrow.ecommerce.model.Category;
import com.marketrow.ecommerce.model.Product;
import com.marketrow.ecommerce.repository.CategoryRepository;
import com.marketrow.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds the database with demo categories and products the first time the app runs
 * against an empty database. Safe to restart the app repeatedly - it will not duplicate data.
 *
 * Product images are pulled from picsum.photos using a fixed seed per product so the same
 * image always renders for the same item. Swap imageUrl values for your own hosted images
 * (e.g. S3 bucket URLs) whenever you're ready.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return; // already seeded
        }

        Category electronics = save(new Category("Electronics", "electronics",
                "Phones, laptops, audio and everyday tech.", img("electronics-cover")));
        Category fashion = save(new Category("Fashion", "fashion",
                "Apparel, footwear and accessories for everyone.", img("fashion-cover")));
        Category home = save(new Category("Home & Kitchen", "home-kitchen",
                "Furniture, cookware and décor for every room.", img("home-cover")));
        Category books = save(new Category("Books", "books",
                "Bestsellers, fiction and non-fiction reads.", img("books-cover")));
        Category sports = save(new Category("Sports & Outdoors", "sports-outdoors",
                "Gear for fitness, camping and outdoor adventure.", img("sports-cover")));
        Category beauty = save(new Category("Beauty & Personal Care", "beauty",
                "Skincare, haircare and grooming essentials.", img("beauty-cover")));

        // ---------- Electronics ----------
        saveProduct("Wireless Noise-Cancelling Headphones",
                "Over-ear Bluetooth headphones with active noise cancellation and 30-hour battery life.",
                "6999.00", "headphones-1", 45, true, electronics);
        saveProduct("Smartphone 128GB",
                "6.5-inch AMOLED display, triple camera system, 5G-ready.",
                "24999.00", "smartphone-1", 20, true, electronics);
        saveProduct("Slim Laptop 14-inch",
                "Lightweight ultrabook with 16GB RAM, 512GB SSD, all-day battery.",
                "58999.00", "laptop-1", 12, true, electronics);
        saveProduct("Smartwatch Fitness Tracker",
                "Heart-rate monitoring, GPS, sleep tracking and 7-day battery.",
                "3499.00", "smartwatch-1", 60, false, electronics);
        saveProduct("Portable Bluetooth Speaker",
                "Waterproof compact speaker with 12-hour playtime.",
                "1799.00", "speaker-1", 80, false, electronics);
        saveProduct("4K Action Camera",
                "Ultra HD action camera with waterproof case and image stabilization.",
                "8999.00", "camera-1", 25, false, electronics);

        // ---------- Fashion ----------
        saveProduct("Men's Slim Fit Denim Jacket",
                "Classic wash denim jacket with a modern slim fit.",
                "2199.00", "jacket-1", 40, true, fashion);
        saveProduct("Women's Floral Summer Dress",
                "Lightweight breathable fabric, perfect for warm days.",
                "1499.00", "dress-1", 55, true, fashion);
        saveProduct("Running Sneakers",
                "Cushioned sole running shoes with breathable mesh upper.",
                "2999.00", "sneakers-1", 70, false, fashion);
        saveProduct("Leather Crossbody Bag",
                "Genuine leather crossbody bag with adjustable strap.",
                "3299.00", "bag-1", 30, false, fashion);
        saveProduct("Classic Aviator Sunglasses",
                "UV-protected polarized lenses with metal frame.",
                "999.00", "sunglasses-1", 90, false, fashion);
        saveProduct("Unisex Wool Beanie",
                "Soft knit beanie for cold weather styling.",
                "499.00", "beanie-1", 100, false, fashion);

        // ---------- Home & Kitchen ----------
        saveProduct("Non-Stick Cookware Set (5-Piece)",
                "Durable non-stick cookware set with heat-resistant handles.",
                "3999.00", "cookware-1", 35, true, home);
        saveProduct("Memory Foam Pillow (Set of 2)",
                "Ergonomic contour pillows for better neck support.",
                "1599.00", "pillow-1", 65, false, home);
        saveProduct("Stainless Steel Electric Kettle",
                "1.7L rapid-boil kettle with auto shut-off.",
                "1299.00", "kettle-1", 50, true, home);
        saveProduct("Wooden Coffee Table",
                "Solid wood coffee table with minimalist design.",
                "6499.00", "table-1", 15, false, home);
        saveProduct("Cotton Bedsheet Set (Queen)",
                "Breathable 300-thread-count cotton bedsheet with 2 pillow covers.",
                "1899.00", "bedsheet-1", 45, false, home);
        saveProduct("LED Desk Lamp",
                "Adjustable brightness desk lamp with USB charging port.",
                "899.00", "lamp-1", 75, false, home);

        // ---------- Books ----------
        saveProduct("The Art of Clear Thinking",
                "A practical guide to better decision-making and mental clarity.",
                "399.00", "book-1", 120, true, books);
        saveProduct("Modern Web Development Handbook",
                "A hands-on guide covering full-stack web development fundamentals.",
                "599.00", "book-2", 90, true, books);
        saveProduct("Mystery at Rivermill",
                "A gripping page-turner mystery novel set in a small town.",
                "349.00", "book-3", 100, false, books);
        saveProduct("The Entrepreneur's Playbook",
                "Actionable strategies for building and scaling a business.",
                "499.00", "book-4", 70, false, books);
        saveProduct("World Atlas & Almanac",
                "Comprehensive maps and facts about every country in the world.",
                "699.00", "book-5", 40, false, books);

        // ---------- Sports & Outdoors ----------
        saveProduct("Yoga Mat with Carry Strap",
                "Non-slip, extra-thick yoga mat, eco-friendly material.",
                "899.00", "yoga-1", 85, true, sports);
        saveProduct("Adjustable Dumbbell Set",
                "Space-saving adjustable dumbbells, 5-25kg per hand.",
                "5999.00", "dumbbell-1", 20, true, sports);
        saveProduct("2-Person Camping Tent",
                "Waterproof, easy-setup tent for weekend camping trips.",
                "3499.00", "tent-1", 18, false, sports);
        saveProduct("Insulated Water Bottle 1L",
                "Keeps drinks cold for 24 hours or hot for 12 hours.",
                "699.00", "bottle-1", 110, false, sports);
        saveProduct("Cycling Helmet",
                "Lightweight, ventilated helmet with adjustable fit dial.",
                "1499.00", "helmet-1", 40, false, sports);

        // ---------- Beauty & Personal Care ----------
        saveProduct("Vitamin C Face Serum",
                "Brightening serum with vitamin C and hyaluronic acid.",
                "799.00", "serum-1", 95, true, beauty);
        saveProduct("Argan Oil Hair Treatment",
                "Nourishing hair oil for shine and frizz control.",
                "599.00", "hairoil-1", 80, false, beauty);
        saveProduct("Electric Trimmer Kit",
                "Cordless grooming kit with multiple attachment guards.",
                "1999.00", "trimmer-1", 30, false, beauty);
        saveProduct("Natural Clay Face Mask",
                "Deep-cleansing clay mask for all skin types.",
                "449.00", "facemask-1", 100, false, beauty);
        saveProduct("Luxury Perfume 100ml",
                "Long-lasting fragrance with notes of amber and sandalwood.",
                "2499.00", "perfume-1", 50, false, beauty);
    }

    private Category save(Category category) {
        return categoryRepository.save(category);
    }

    private void saveProduct(String name, String description, String price, String imageSeed,
                              int stock, boolean featured, Category category) {
        Product product = new Product(name, description, new BigDecimal(price), img(imageSeed),
                stock, featured, category);
        productRepository.save(product);
    }

    private String img(String seed) {
        return "https://picsum.photos/seed/" + seed + "/600/600";
    }
}
