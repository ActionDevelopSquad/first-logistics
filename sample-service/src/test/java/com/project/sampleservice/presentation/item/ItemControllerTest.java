package com.project.sampleservice.presentation.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sampleservice.application.item.ItemCommandService;
import com.project.sampleservice.application.item.ItemQueryService;
import com.project.sampleservice.domain.item.exception.ItemErrorCode;
import com.project.sampleservice.domain.item.exception.ItemException;
import com.project.sampleservice.presentation.item.dto.CreateItemRequest;
import com.project.sampleservice.presentation.item.dto.ItemResponse;
import com.project.sampleservice.presentation.item.dto.UpdateItemRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean ItemCommandService itemCommandService;
    @MockitoBean ItemQueryService itemQueryService;

    @Nested
    @DisplayName("POST /api/items")
    class CreateItem {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            given(itemCommandService.createItem(any())).willReturn(1L);

            mockMvc.perform(post("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new CreateItemRequest("테스트 아이템", "설명"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value("COMMON_S002"))
                    .andExpect(jsonPath("$.data").value(1));
        }

        @Test
        @DisplayName("실패 - name 누락 시 400")
        void fail_nameMissing() throws Exception {
            mockMvc.perform(post("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new CreateItemRequest("", "설명"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("COMMON_001"));
        }
    }

    @Nested
    @DisplayName("GET /api/items")
    class GetItems {

        @Test
        @DisplayName("성공 - 전체 조회")
        void success() throws Exception {
            given(itemQueryService.getItems()).willReturn(List.of(
                    new ItemResponse(1L, "아이템1", "설명1", LocalDateTime.now(), LocalDateTime.now()),
                    new ItemResponse(2L, "아이템2", "설명2", LocalDateTime.now(), LocalDateTime.now())
            ));

            mockMvc.perform(get("/api/items"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("COMMON_S001"))
                    .andExpect(jsonPath("$.data.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/items/{id}")
    class GetItem {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            given(itemQueryService.getItem(1L)).willReturn(
                    new ItemResponse(1L, "아이템", "설명", LocalDateTime.now(), LocalDateTime.now()));

            mockMvc.perform(get("/api/items/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("COMMON_S001"))
                    .andExpect(jsonPath("$.data.id").value(1));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID 시 404")
        void fail_notFound() throws Exception {
            given(itemQueryService.getItem(999L))
                    .willThrow(new ItemException(ItemErrorCode.ITEM_NOT_FOUND));

            mockMvc.perform(get("/api/items/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("ITEM_001"))
                    .andExpect(jsonPath("$.message").value("아이템을 찾을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("PUT /api/items/{id}")
    class UpdateItem {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            given(itemCommandService.updateItem(any())).willReturn(1L);

            mockMvc.perform(put("/api/items/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new UpdateItemRequest("수정된 이름", "수정된 설명"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("COMMON_S001"))
                    .andExpect(jsonPath("$.data").value(1));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID 시 404")
        void fail_notFound() throws Exception {
            given(itemCommandService.updateItem(any()))
                    .willThrow(new ItemException(ItemErrorCode.ITEM_NOT_FOUND));

            mockMvc.perform(put("/api/items/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new UpdateItemRequest("수정된 이름", "수정된 설명"))))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("ITEM_001"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/items/{id}")
    class DeleteItem {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            willDoNothing().given(itemCommandService).deleteItem(1L);

            mockMvc.perform(delete("/api/items/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("COMMON_S003"));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID 시 404")
        void fail_notFound() throws Exception {
            willThrow(new ItemException(ItemErrorCode.ITEM_NOT_FOUND))
                    .given(itemCommandService).deleteItem(999L);

            mockMvc.perform(delete("/api/items/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("ITEM_001"));
        }
    }
}
