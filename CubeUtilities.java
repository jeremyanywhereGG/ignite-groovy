
public class CubeUtilities {
   static int[] TOP_ROW = new int[] {9,10,11,12,30,31,32,33,0,1,2,20,41,21,22,23};
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
   static void test1(int[] source, int[] target) {


   }

}
