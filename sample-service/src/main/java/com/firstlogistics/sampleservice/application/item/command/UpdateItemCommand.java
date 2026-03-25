package com.project.sampleservice.application.item.command;

public record UpdateItemCommand(Long id, String name, String description) {
}
