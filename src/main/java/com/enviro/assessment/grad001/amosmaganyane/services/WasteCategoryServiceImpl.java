package com.enviro.assessment.grad001.amosmaganyane.services;

import com.enviro.assessment.grad001.amosmaganyane.models.WasteCategory;
import com.enviro.assessment.grad001.amosmaganyane.repositories.WasteCategoryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the WasteCategoryService interface.
 * Handles business logic for WasteCategory entities.
 */
public class WasteCategoryServiceImpl implements WasteCategoryService {
    public final WasteCategoryRepository repository;

    public WasteCategoryServiceImpl(WasteCategoryRepository repository){
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     * Saves a new category to the database.
     */
    @Override
    public WasteCategory createCategory(WasteCategory category) {
        return repository.save(category);
    }

    /**
     * {@inheritDoc}
     * Fetches a category by ID using repository's findById method.
     */
    @Override
    public Optional<WasteCategory> getCategoryById(Long id) {
        return repository.findById(id);
    }

    /**
     * {@inheritDoc}
     * Retrieves all categories using repository's findAll method.
     */
    @Override
    public List<WasteCategory> getAllCategories() {
        return repository.findAll();
    }

    @Override
    public WasteCategory updateCategory(Long id, WasteCategory category) {
        // TODO: later figure out if more logic is needed for updating
        return null;
    }


    /**
     * {@inheritDoc}
     * Deletes a category by ID using repository's deleteById method.
     */
    @Override
    public void deleteCategory(Long id) {
        repository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     * Checks if a category name is unique by verifying it doesn't already exist in the database.
     *
     * @param name the name of the category to check
     * @return true if the category name is unique, false otherwise
     */
    @Override
    public boolean isCategoryNameUnique(String name) {
        return !repository.existsByNameIgnoreCase(name);
    }

    /**
     * {@inheritDoc}
     * Searches for categories whose names contain the given keyword (case-insensitive).
     * If the keyword is null or empty, retrieves all categories.
     *
     * @param keyword the keyword to search for
     * @return a list of categories matching the keyword or all categories if the keyword is empty
     */
    @Override
    public List<WasteCategory> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()){
            return getAllCategories();
        }
        return repository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * {@inheritDoc}
     * Validates a category name based on a few rules:
     * - Name must not be null or empty.
     * - Name length must be between 3 and 50 characters.
     *
     * @param name the category name to validate
     * @return true if the name is valid, false otherwise
     */
    @Override
    public boolean isValidCategoryName(String name) {
        return name != null
                && !name.trim().isEmpty()
                && name.length() >= 3
                && name.length() <= 50;
    }
}
