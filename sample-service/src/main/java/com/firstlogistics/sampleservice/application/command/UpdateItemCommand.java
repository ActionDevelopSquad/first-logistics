package com.firstlogistics.sampleservice.application.command;

public record UpdateItemCommand(Long id, String name, String description) {
}
