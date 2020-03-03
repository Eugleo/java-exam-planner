package com.wybitul.planex.solver;

import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*
Runs a model and creates Result object from the solution.
 */

public class Solver {
    public final Model model;
    public final CpSolver solver = new CpSolver();
    public final LocalDate beginning;

    public Solver(Model model) {
        this.model = model;
        this.beginning = model.beginning;
    }

    public Set<Result> solve(Consumer<CpSolverStatus> statusConsumer) {
        CpSolverStatus status = solver.solve(model.model);

        if (statusConsumer != null) {
            statusConsumer.accept(status);
        }
        if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
            return new HashSet<>();
        }

        return model.classModels.stream()
                .map(cm -> new Result(cm.classOptions, varToDate(cm.end), varToDate(cm.start).plusDays(1),
                        varToInt(cm.backupTries), varToInt(cm.preparationTime)))
                .collect(Collectors.toSet());
    }

    private int varToInt(IntVar n) {
        return Math.toIntExact(solver.value(n));
    }

    private LocalDate varToDate(IntVar n) {
        return beginning.plusDays(solver.value(n));
    }
}
