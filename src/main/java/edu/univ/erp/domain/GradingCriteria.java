package edu.univ.erp.domain;

public class GradingCriteria {
    private int criteriaId;
    private int sectionId;
    private String componentName;
    private double weightPercentage;
    private int displayOrder;

    // Constructors
    public GradingCriteria() {}
    
    public GradingCriteria(int sectionId, String componentName, double weightPercentage, int displayOrder) {
        this.sectionId = sectionId;
        this.componentName = componentName;
        this.weightPercentage = weightPercentage;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public int getCriteriaId() { return criteriaId; }
    public void setCriteriaId(int criteriaId) { this.criteriaId = criteriaId; }
    
    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    
    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }
    
    public double getWeightPercentage() { return weightPercentage; }
    public void setWeightPercentage(double weightPercentage) { this.weightPercentage = weightPercentage; }
    
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}