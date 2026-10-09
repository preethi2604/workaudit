package com.workaudit.workaudit.report.entity;

public enum DescriptionLevel {
    GREEN,   // proper, sufficiently detailed
    YELLOW,  // present but too short/generic - needs review
    RED      // required sections missing / clearly incomplete
}