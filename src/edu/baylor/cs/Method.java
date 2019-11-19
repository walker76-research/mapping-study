package edu.baylor.cs;

public enum Method {
    LEARNING("Learning"), TOKENS("Tokens"), TEXT("Text"), AST("AST"), SEMANTIC("Semantic"), PDG("PDG"), METRIC("Metric"), MINING("Data Mining");

    private String name;

    private Method(String name){
        this.name = name;
    }

    public String getString(){
        return name;
    }
}
