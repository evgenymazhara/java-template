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
    Matrix m1 = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\m1.txt");
    Matrix m2 = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\m2.txt");
    Matrix expected = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\result12.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulDS() throws Exception{
    Matrix m1 = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\m1.txt");
    Matrix m2 = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\m3.txt");
    Matrix expected = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\result13.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulSD() throws Exception{
    Matrix m1 = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\m4.txt");
    Matrix m2 = new DenseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\m1.txt");
    Matrix expected = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\result41.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulSS() throws Exception{
    Matrix m1 = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\m3.txt");
    Matrix m2 = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\m4.txt");
    Matrix expected = new SparseMatrix("C:\\Users\\Евгений\\IdeaProjects\\java-template\\src\\test\\java\\edu\\spbu\\matrix\\result34.txt");
    assertEquals(expected, m1.mul(m2));
  }
}
