package com.wybitul.examplanner;

import com.google.ortools.sat.IntVar;

public class ClassModel {
    final ClassOptions classOptions;
    final IntVar start;
    final IntVar end;
    final IntVar preparationTime;
    final IntVar backupTries;

    public ClassModel(ClassOptions classOptions, IntVar start, IntVar end, IntVar preparationTime, IntVar backupTries) {
        this.classOptions = classOptions;
        this.start = start;
        this.end = end;
        this.preparationTime = preparationTime;
        this.backupTries = backupTries;
    }
}
