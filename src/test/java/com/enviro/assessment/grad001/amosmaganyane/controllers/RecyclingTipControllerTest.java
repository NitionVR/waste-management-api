package com.enviro.assessment.grad001.amosmaganyane.controllers;

import com.enviro.assessment.grad001.amosmaganyane.dto.RecyclingTipDTO;
import com.enviro.assessment.grad001.amosmaganyane.models.RecyclingTip;
import com.enviro.assessment.grad001.amosmaganyane.models.WasteCategory;
import com.enviro.assessment.grad001.amosmaganyane.services.RecyclingTipService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecyclingTipController.class)
@DisplayName("Recycling Tips API Tests")
class RecyclingTipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecyclingTipService tipService;

    @Autowired
    private ObjectMapper objectMapper;

    private WasteCategory testCategory;
    private RecyclingTip testTip;

    @BeforeEach
    void initializeModels() {
        testCategory = new WasteCategory(1L, "Recyclable", "Description");
        testTip = new RecyclingTip(1L, "Paper Recycling",
                "How to recycle paper properly", testCategory);
    }

    @Test
    @DisplayName("POST /categories/{categoryId}/tips - Should create a new recycling tip")
    void testCreateTip() throws Exception {
        when(tipService.createTip(eq(1L), any(RecyclingTip.class))).thenReturn(testTip);

        mockMvc.perform(post("/wastemanagementapi/categories/1/tips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTip)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Paper Recycling"));
    }

    @Test
    @DisplayName("POST /categories/{categoryId}/tips - Should return 400 for invalid tip data")
    void testReturnBadRequestWhenCreatingInvalidTip() throws Exception {
        when(tipService.createTip(eq(1L), any(RecyclingTip.class)))
                .thenThrow(new IllegalArgumentException("Invalid tip"));

        mockMvc.perform(post("/wastemanagementapi/categories/1/tips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTip)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /tips/{id} - Should return a tip when it exists")
    void testGetTipById() throws Exception {
        when(tipService.getTipById(1L)).thenReturn(Optional.of(testTip));

        mockMvc.perform(get("/wastemanagementapi/tips/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Paper Recycling"));
    }

    @Test
    @DisplayName("GET /tips/{id} - Should return 404 when tip not found")
    void testReturn404WhenTipNotFound() throws Exception {
        when(tipService.getTipById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/wastemanagementapi/tips/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /tips - Should return all recycling tips")
    void testGetAllTips() throws Exception {
        List<RecyclingTip> tips = List.of(
                testTip,
                new RecyclingTip(2L, "Glass Recycling", "How to recycle glass", testCategory)
        );
        when(tipService.getAllTips()).thenReturn(tips);

        mockMvc.perform(get("/wastemanagementapi/tips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Paper Recycling"))
                .andExpect(jsonPath("$[1].title").value("Glass Recycling"));
    }

    @Test
    @DisplayName("PUT /tips/{id} - Should update an existing tip")
    void testUpdateTip() throws Exception {
        Long tipId = 1L;
        RecyclingTipDTO updateRequest = new RecyclingTipDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated content");

        RecyclingTip existingTip = new RecyclingTip(tipId, "Old Title",
                "Old content", testCategory);
        RecyclingTip updatedTip = new RecyclingTip(tipId, "Updated Title",
                "Updated content", testCategory);

        when(tipService.getTipById(tipId))
                .thenReturn(Optional.of(existingTip));
        when(tipService.updateTip(eq(tipId), any(RecyclingTip.class)))
                .thenReturn(updatedTip);

        mockMvc.perform(put("/wastemanagementapi/tips/" + tipId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tipId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.categoryId").value(testCategory.getId()))
                .andExpect(jsonPath("$.categoryName").value(testCategory.getName()));
    }

    @Test
    @DisplayName("PUT /tips/{id} - Should return 404 when tip not found")
    void testUpdateNonExistentTip() throws Exception {
        Long nonExistentId = 999L;
        RecyclingTipDTO updateRequest = new RecyclingTipDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated content");

        when(tipService.getTipById(nonExistentId))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/wastemanagementapi/tips/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /tips/{id} - Should delete a tip")
    void testDeleteTip() throws Exception {
        mockMvc.perform(delete("/wastemanagementapi/tips/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /categories/{categoryId}/tips - Should return tips for a category")
    void shouldGetTipsByCategory() throws Exception {
        List<RecyclingTip> tips = List.of(testTip);
        when(tipService.getTipsByCategory(1L)).thenReturn(tips);

        mockMvc.perform(get("/wastemanagementapi/categories/1/tips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Paper Recycling"));
    }

    @Test
    @DisplayName("GET /tips/search - Should return tips matching search keyword")
    void testSearchTips() throws Exception {
        String keyword = "paper";
        List<RecyclingTip> searchResults = List.of(
                new RecyclingTip(1L, "Paper Recycling", "Content about paper", testCategory),
                new RecyclingTip(2L, "Newspaper Disposal", "How to recycle newspaper", testCategory)
        );
        when(tipService.searchTips(keyword)).thenReturn(searchResults);

        mockMvc.perform(get("/wastemanagementapi/tips/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Paper Recycling"))
                .andExpect(jsonPath("$[1].title").value("Newspaper Disposal"));
    }

    @Test
    @DisplayName("GET /tips/search - Should return all tips when keyword is empty")
    void testSearchTipsWithEmptyKeyword() throws Exception {
        List<RecyclingTip> allTips = List.of(
                new RecyclingTip(1L, "Paper Recycling", "Content 1", testCategory),
                new RecyclingTip(2L, "Glass Recycling", "Content 2", testCategory)
        );
        when(tipService.searchTips("")).thenReturn(allTips);

        mockMvc.perform(get("/wastemanagementapi/tips/search")
                        .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /tips/search - Should return empty list when no matches found")
    void testSearchTipsNoMatches() throws Exception {
        String keyword = "nonexistent";
        when(tipService.searchTips(keyword)).thenReturn(List.of());

        mockMvc.perform(get("/wastemanagementapi/tips/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}