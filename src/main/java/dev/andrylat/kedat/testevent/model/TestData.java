package dev.andrylat.kedat.testevent.model;

import lombok.extern.jackson.Jacksonized;

public record TestData(String name, boolean isActive, int x) {}
