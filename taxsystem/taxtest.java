

public class taxtest {
    public static void main(String[] args) {
        // Create a tax system instance
        TaxSystem taxSystem = new TaxSystem();

        // Open the detailed form
        new DetailedTaxForm(taxSystem);

        System.out.println("Tax application started successfully!");
    }
}
