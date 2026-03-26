package com.firstlogistics.sampleservice.presentation;

import com.firstlogistics.sampleservice.application.ItemCommandService;
import com.firstlogistics.sampleservice.application.ItemQueryService;
import com.firstlogistics.sampleservice.presentation.dto.CreateItemRequest;
import com.firstlogistics.sampleservice.presentation.dto.ItemResponse;
import com.firstlogistics.sampleservice.presentation.dto.UpdateItemRequest;
import common.response.ApiResponse;
import common.response.CommonSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemCommandService itemCommandService;
    private final ItemQueryService itemQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createItem(@RequestBody @Valid CreateItemRequest request) {
        Long id = itemCommandService.createItem(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(CommonSuccessCode.CREATED, id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getItems() {
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, itemQueryService.getItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, itemQueryService.getItem(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateItem(
            @PathVariable Long id,
            @RequestBody @Valid UpdateItemRequest request
    ) {
        Long updatedId = itemCommandService.updateItem(request.toCommand(id));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, updatedId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        itemCommandService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.DELETED, null));
    }
}
