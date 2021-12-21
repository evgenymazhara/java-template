package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixTest
{
  /**
   * ожидается 4 таких теста
   */
  @Test
  public void mulDD() throws Exception {
    Matrix m1 = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\dm1.txt");
    Matrix m2 = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\dm2.txt");
    Matrix expected = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\dm1xdm2.txt");
    long startTime = System.nanoTime();
    assertEquals(expected, m1.mul(m2));
    long singleThreadTime = System.nanoTime() - startTime;
    System.out.print("Single thread Dense x Dense matrices multiplication time: " + singleThreadTime/1000 + " ms" + "\n");
    startTime = System.nanoTime();
    assertEquals(expected, m1.dmul(m2));
    long multiThreadTime = System.nanoTime() - startTime;
    System.out.print("Multi thread Dense x Dense matrices multiplication time: " + multiThreadTime/1000 + " ms" + "\n");
  }
  @Test
  public void mulDS() throws Exception{
    Matrix m1 = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\dm1.txt");
    Matrix m2 = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\sm2.txt");
    Matrix expected = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\dm1xsm2.txt");
    long startTime = System.nanoTime();
    assertEquals(expected, m1.mul(m2));
    long singleThreadTime = System.nanoTime() - startTime;
    System.out.print("Single thread Dense x Sparse matrices multiplication time: " + singleThreadTime/1000 + " ms" + "\n");
    startTime = System.nanoTime();
    assertEquals(expected, m1.dmul(m2));
    long multiThreadTime = System.nanoTime() - startTime;
    System.out.print("Multi thread Dense x Sparse matrices multiplication time: " + multiThreadTime/1000 + " ms" + "\n");
  }
  @Test
  public void mulSD() throws Exception{
    Matrix m1 = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\sm1.txt");
    Matrix m2 = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\dm2.txt");
    Matrix expected = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\sm1xdm2.txt");
    long startTime = System.nanoTime();
    assertEquals(expected, m1.mul(m2));
    long singleThreadTime = System.nanoTime() - startTime;
    System.out.print("Single thread Sparse x Dense matrices multiplication time: " + singleThreadTime/1000 + " ms" + "\n");
    startTime = System.nanoTime();
    assertEquals(expected, m1.dmul(m2));
    long multiThreadTime = System.nanoTime() - startTime;
    System.out.print("Multi thread Sparse x Dense matrices multiplication time: " + multiThreadTime/1000 + " ms" + "\n");
  }
  @Test
  public void mulSS() throws Exception{
    Matrix m1 = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\sm1.txt");
    Matrix m2 = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\sm2.txt");
    Matrix expected = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\sm1xsm2.txt");
    long startTime = System.nanoTime();
    assertEquals(expected, m1.mul(m2));
    long singleThreadTime = System.nanoTime() - startTime;
    System.out.print("Single thread Sparse x Sparse matrices multiplication time: " + singleThreadTime/1000 + " ms" + "\n");
    startTime = System.nanoTime();
    assertEquals(expected, m1.dmul(m2));
    long multiThreadTime = System.nanoTime() - startTime;
    System.out.print("Multi thread Sparse x Sparse matrices multiplication time: " + multiThreadTime/1000 + " ms" + "\n");
  }
}
