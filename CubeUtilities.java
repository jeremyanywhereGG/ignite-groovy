import java.util.ArrayList;
import java.util.Arrays;

public class CubeUtilities {
   static int[] TOP_ROW = new int[] {9,10,11,12,30,31,32,33,0,1,2,20,41,21,22,23};
   static String[] moves = {"front_anticlock", "left_col_rot", "back_anticlock", "front_180", "top_row_rot",
   "middle_row_rot", "right_col_rot", "back_180", "front_clock", "middle_col_rot",
   "bottom_row_rot", "back_clock"};
   static int[] getStartPosition() {
      int[] myList = new int[42];
      // Fill the ArrayList with 41 elements, each value being equal to its index
      for (int i = 0; i < 42; i++) {
          myList[i]=i;
      }
      return myList;
   }
   static int[] geFullWildcardPosition() {
      int[] myList = new int[42];
      // Fill the ArrayList with 41 elements, each value being equal to its index
      for (int i = 0; i < 42; i++) {
          myList[i]=-1;
      }
      return myList;
   }
   static void fillSpecificSquares(int[] source, int[]squares) {
      for (int i = 0; i < squares.length; i++) {
          source[squares[i]]=squares[i];
      }
   }
   static void copySquares(int[] source, int[] target) {
      for (int i = 0; i < source.length; i++) {
          target[i] = source[i];
      }
   }
   /**
    * defines source and target positions for moving an edge piece from the bottom up to the side in a 3 X 3 X 2
    * @return
    */
   static ArrayList<int[]> moveFitEdgeBottomToSide() {
      ArrayList<int[]> move = new ArrayList<int[]>();
      int[] source = CubeUtilities.geFullWildcardPosition();
      int[] squares = {0, 1, 2, 21, 22, 23, 9, 10, 11, 30, 31, 32, 20, 41, 3, 4, 5, 24, 25, 26, 19, 40, 7};
      CubeUtilities.fillSpecificSquares(source,squares);
      int[] target = Arrays.copyOf(source, source.length);
      target[5] = source[7];
      target[7] = '?';
      move.add(0, source);
      move.add(1, target);
      return move;
   }

}
