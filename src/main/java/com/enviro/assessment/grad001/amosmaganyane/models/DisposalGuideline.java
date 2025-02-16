package com.enviro.assessment.grad001.amosmaganyane.models;

import jakarta.persistence.*;
import java.util.Objects;

// Represents a disposal guideline entity with many-to-one relationship to WasteCategory
@Entity
@Table(name = "disposal_guidelines")
public class DisposalGuideline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title and instructions are required fields to make recycling tips meaningful
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String instructions;

    //Each disposal guideline belongs to a specific waste category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private WasteCategory category;

    public DisposalGuideline(){
    }

    public DisposalGuideline (Long id, String title, String instructions, WasteCategory category){
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public WasteCategory getCategory() {
        return category;
    }

    public void setCategory(WasteCategory category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisposalGuideline that = (DisposalGuideline) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(instructions, that.instructions) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, instructions, category);
    }

    @Override
    public String toString() {
        return "DisposalGuideline{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", instructions='" + instructions + '\'' +
                ", category=" + category +
                '}';
    }

}
