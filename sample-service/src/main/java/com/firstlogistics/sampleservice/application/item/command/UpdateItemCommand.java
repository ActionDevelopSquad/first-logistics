package com.firstlogistics.sampleservice.application.item.command;

public record UpdateItemCommand(Long id, String name, String description) {
}
