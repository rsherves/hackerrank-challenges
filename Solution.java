import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Solution {

    // Complete the matrixRotation function below.
    static void matrixRotation(List<List<Integer>> matrix, int r) {
        /*
         1) Identify rings, disassemble the matrix in rings
         2) Treat each ring individually
           2.1) Unfold the ring to represent it as a List
           2.2) Each ring rotates r%N times (N = number of elements in ring)
           2.3) Each rotation just means iterating the list starting at +1
         3) Assemble the new matrix with the 1D lists (unfolded rings)
         */
        final int numRows = matrix.size();
        final int numCols = matrix.get(0).size();
        final int numRings = Math.min(numRows, numCols)/2;

        List<List<Integer>> unfoldedRings = toListOfUnfoldedRings(matrix);
        int[] ringRotations = getNumRotationsPerRing(unfoldedRings, r);
        print(toMatrix(numRows, numCols, unfoldedRings, ringRotations));
    }

    private static List<List<Integer>> toListOfUnfoldedRings(
            List<List<Integer>> matrix) {
        final int numRows = matrix.size();
        final int numCols = matrix.get(0).size();
        final int numRings = Math.min(numRows, numCols)/2;
        List<List<Integer>> toret = new ArrayList();

        for (int ring=0; ring < numRings; ring++) {
            final int startX = ring;
            final int endX = numCols - 1 - ring;
            final int startY = ring;
            final int endY = numRows - 1 - ring;
            List<Integer> unfoldedRing = new ArrayList();

            // upper row
            for (int x =startX; x <= endX; x++) {
                unfoldedRing.add(matrix.get(startY).get(x));
            }
            // right col
            for (int y = startY+1; y <= endY; y++) {
                unfoldedRing.add(matrix.get(y).get(endX));
            }
            // bottom row
            for (int x = endX-1; x >= startX; x--) {
                unfoldedRing.add(matrix.get(endY).get(x));
            }
            // left col
            for (int y = endY-1; y >= startY+1; y--) {
                unfoldedRing.add(matrix.get(y).get(startX));
            }
            toret.add(unfoldedRing);
        }
        return toret;
    }

    private static int[] getNumRotationsPerRing(
            List<List<Integer>> unfoldedRings, int matrixRotations) {
        int numRings = unfoldedRings.size();
        int[] ringRotations = new int[numRings];
        for (int i=0; i<unfoldedRings.size() ; i++) {
            int numRingElements = unfoldedRings.get(i).size();
            ringRotations[i] = matrixRotations % numRingElements;
        }
        return ringRotations;
    }

    private static Integer[][] toMatrix(
            int matrixRows,
            int matrixCols,
            List<List<Integer>> unfoldedRings,
            int[] ringRotations) {

        if (unfoldedRings.size() != ringRotations.length) {
            throw new RuntimeException("Cannot reconstruct the matrix");
        }
        final Integer[][] matrix = new Integer[matrixRows][matrixCols];
        final int numRings = Math.min(matrixRows, matrixCols)/2;

        for (int ring=0; ring<numRings; ring++) {
            final int startX = ring;
            final int endX = matrixCols - 1 - ring;
            final int startY = ring;
            final int endY = matrixRows - 1 - ring;
            final int ringCols = endX - startX + 1;
            final int ringRows = endY - startY + 1;
            final int ringNumElements = unfoldedRings.get(ring).size();
            final int offset = ringRotations[ring];

            // upper row
            for (int i=0; i<ringCols; i++) {
                int pos = (offset + i) % ringNumElements;
                matrix[startY][startX + i] = unfoldedRings.get(ring).get(pos);
            }
            // right col
            for (int i=0; i<ringRows-1; i++) {
                int pos = (ringCols + offset + i) % ringNumElements;
                matrix[startY + 1 + i][endX] = unfoldedRings.get(ring).get(pos);
            }
            // bottom row
            for (int i=0; i<ringCols-1; i++) {
                int pos = (ringCols + (ringRows-1) + offset + i) % ringNumElements;
                matrix[endY][endX - 1 - i] = unfoldedRings.get(ring).get(pos);
            }
            // left col
            for (int i=0; i<ringRows - 2; i++) {
                int pos = (ringCols + (ringRows-1) + (ringCols-1) + offset + i) 
                        % ringNumElements;
                matrix[endY - 1 - i][startX] = unfoldedRings.get(ring).get(pos);
            }
        }
        return matrix;
    }

    static void print(Integer[][] matrix) {
        Stream.of(matrix)
                .map(row -> {
                    return Stream.of(row)
                            .map(String::valueOf)
                            .collect(Collectors.joining(" "));
                })
                .forEach(System.out::println);
    }


    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(System.in));

        String[] mnr = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        int m = Integer.parseInt(mnr[0]);

        int n = Integer.parseInt(mnr[1]);

        int r = Integer.parseInt(mnr[2]);

        List<List<Integer>> matrix = new ArrayList<>();

        IntStream.range(0, m).forEach(i -> {
            try {
                matrix.add(
                    Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                        .map(Integer::parseInt)
                        .collect(toList())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        matrixRotation(matrix, r);

        bufferedReader.close();
    }
}
