package com.wybitul.planex.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
Crude class used to simplify manipulation with two dimensional arrays.
 */

@SuppressWarnings("unused")
public class Matrix<T> {
    final List<List<T>> columns = new ArrayList<>();

    public void addColumn(Collection<T> col) {
        columns.add(new ArrayList<>(col));
    }

    public void addColumn(int index, Collection<T> col) {
        columns.add(index, List.copyOf(col));
    }

    public void addRow(Collection<T> row) {
        List<T> rowList = List.copyOf(row);
        IntStream.range(0, row.size()).forEach(i -> columns.get(i).add(rowList.get(i)));
    }

    public void addRow(int index, Collection<T> row) {
        List<T> rowList = List.copyOf(row);
        IntStream.range(0, row.size()).forEach(i -> columns.get(i).add(index, rowList.get(i)));
    }

    public T[][] toArray(IntFunction<T[]> generator, IntFunction<T[][]> generator2) {
        return columns.stream()
                .map(c -> c.toArray(generator))
                .collect(Collectors.toList()).toArray(generator2);
    }

    public int[][] toIntArray() {
        int cols = columns.size();
        int rows = cols > 0 ? columns.get(0).size() : 0;

        int[][] result = new int[rows][cols];
        IntStream.range(0, rows).forEach(i ->
                IntStream.range(0, cols).forEach(j ->
                        result[i][j] = (int) columns.get(j).get(i)
                )
        );
        return result;
    }

    public List<List<T>> getColumns() {
        return columns;
    }

    public List<List<T>> getRows() {
        int cols = columns.size();
        int rows = cols > 0 ? columns.get(0).size() : 0;

        return IntStream.range(0, rows).mapToObj(i ->
                IntStream.range(0, cols).mapToObj(j -> columns.get(j).get(i)).collect(Collectors.toList())
        ).collect(Collectors.toList());
    }
}
