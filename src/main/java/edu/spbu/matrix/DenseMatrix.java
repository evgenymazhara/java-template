package edu.spbu.matrix;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
  public int height;
  public int width;
  public double[][] matr;

  public DenseMatrix(double[][] matr){ // конструктор матрицы по массиву
    this.height = matr.length;
    this.width = matr[0].length;
    this.matr = matr;
  }

  public DenseMatrix(int height, int width){ // конструктор нулевой матрицы
      this.height = height;
      this.width = width;
      this.matr = new double[height][width];
      for(int i = 0; i < this.height; i++){
          for(int j = 0; j < this.width; j++)
              this.matr[i][j] = 0;
      }
  }
  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public DenseMatrix(String fileName) {
      try {
          Scanner scanner = new Scanner(new FileReader(fileName));
          if (!scanner.hasNextLine())
              throw new Exception("Пустой файл");
          ArrayList<double[]> matrixLines = new ArrayList<double[]>();
          String line = scanner.nextLine();
          double[] matrixLine = Arrays.stream(line.split(" ")).mapToDouble(Double::parseDouble).toArray();
          matrixLines.add(matrixLine);
          while(scanner.hasNextLine()){
              line = scanner.nextLine();
              matrixLine = Arrays.stream(line.split(" ")).mapToDouble(Double::parseDouble).toArray();
              matrixLines.add(matrixLine);
          }
          this.width = matrixLine.length;
          this.height = matrixLines.size();
          this.matr = new double[this.height][this.width];
          for(int i = 0; i < this.height; i++)
              this.matr[i]=matrixLines.get(i);
      }
      catch (Exception exception){
          exception.printStackTrace();
      }
  }
  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  private DenseMatrix mul(DenseMatrix o) throws Exception{
      if(this.width != o.height)
          throw new Exception("Матрицы не могут быть умножены: несовпадение размеров");
      DenseMatrix result = new DenseMatrix(this.height, o.width);
      for(int i = 0; i < result.height; i++){
          for(int j = 0; j < result.width; j++){
              for(int k = 0; k < this.width; k++){
                  result.matr[i][j] += this.matr[i][k] * o.matr[k][j];
              }
          }
      }
      return result;
  }
  @Override public Matrix mul(Matrix o) throws Exception
  {
      if(o instanceof DenseMatrix){
          return this.mul((DenseMatrix) o);
      }
      else
          return null;
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override public Matrix dmul(Matrix o)
  {
    return null;
  }

  private boolean equals(DenseMatrix o){
      if(this.height != o.height || this.width != o.width)
          return false;
      for(int i = 0; i < this.height; i++)
          for(int j = 0; j < this.width; j++)
              if(this.matr[i][j] != o.matr[i][j])
                  return false;
      return true;
  }
  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {
      if(o instanceof DenseMatrix) {
          return this.equals((DenseMatrix) o);
      }
      else
          return false;
  }

  public String toString(){
      String result = "";
      for(int i = 0; i < this.height; i++){
          for(int j = 0; j < this.width; j++){
              result = result.concat(Double.toString(this.matr[i][j]));
              result = result.concat(" ");
          }
          result = result.concat("\n");
      }
      return result;
  }
  public int hashCode(){
      int result = Arrays.deepHashCode(this.matr);
      result += this.width;
      result *= 29;
      result += this.height;
      return result;
  }
}
