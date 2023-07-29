
public class CubeUtilities {
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
          source[i]=squares[i];
      }
   }
}
