package edu.spbu.matrix;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
              throw new Exception("Пустой файл\n");
          ArrayList<double[]> matrixLines = new ArrayList<double[]>();
          String line = scanner.nextLine();
          double[] matrixLine = Arrays.stream(line.split(" ")).mapToDouble(Double::parseDouble).toArray();
          matrixLines.add(matrixLine);
          this.width = matrixLine.length;
          while(scanner.hasNextLine()){
              line = scanner.nextLine();
              matrixLine = Arrays.stream(line.split(" ")).mapToDouble(Double::parseDouble).toArray();
              if(matrixLine.length != this.width)
                  throw new Exception("Разное количество элементов в строках матрицы\n");
              matrixLines.add(matrixLine);
          }
          this.height = matrixLines.size();
          this.matr = new double[this.height][this.width];
          for(int i = 0; i < this.height; i++)
              this.matr[i]=matrixLines.get(i);
      }
      catch (Exception exception){
          exception.printStackTrace();
      }
  }

  public SparseMatrix denseToSparse(){
      ArrayList<Double> res_nZV = new ArrayList<Double>();
      ArrayList<Integer> res_cI = new ArrayList<Integer>();
      ArrayList<Integer> res_rI = new ArrayList<Integer>();

      for(int i = 0; i < this.height; i++){
          for(int j = 0; j < this.width; j++){
              if(this.matr[i][j] != 0){
                  res_nZV.add(this.matr[i][j]);
                  res_cI.add(j);
              }
          }
          res_rI.add(res_nZV.size());
      }
      SparseMatrix res = new SparseMatrix(this.height, this.width, res_nZV, res_cI, res_rI);
      return res;
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

  private SparseMatrix mul(SparseMatrix o) throws Exception{
      if(this.width != o.height)
          throw new Exception("Матрицы не могут быть умножены: несовпадение размеров");
      SparseMatrix o_tr = o.CRS_transposition();
      ArrayList<Double> res_nZV = new ArrayList<Double>();
      ArrayList<Integer> res_cI = new ArrayList<Integer>();
      ArrayList<Integer> res_rI = new ArrayList<Integer>();
      res_rI.add(0);

      for(int i = 0; i < this.height; i++) {
          for (int j = 0; j < o_tr.height; j++) {
              double scalarProduct = 0;
              for(int k = o_tr.rowsIndexation.get(j); k < o_tr.rowsIndexation.get(j+1); k++){
                  scalarProduct += o_tr.notZeroValues.get(k) * this.matr[i][o_tr.colsIndex.get(k)];
              }
              if(scalarProduct != 0){
                  res_nZV.add(scalarProduct);
                  res_cI.add(j);
              }
          }
          res_rI.add(res_nZV.size());
      }

      SparseMatrix res = new SparseMatrix(this.height, o.width, res_nZV, res_cI, res_rI);
      return res;
  }

  @Override public Matrix mul(Matrix o) throws Exception
  {
      if(o instanceof DenseMatrix){
          return this.mul((DenseMatrix) o);
      }
      else if(o instanceof SparseMatrix){
          return this.mul((SparseMatrix) o);
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
  private DenseMatrix dmul(DenseMatrix o) throws Exception{
      if(this.width != o.height)
          throw new Exception("Матрицы не могут быть умножены: несовпадение размеров");
      double[][] resMatr = new double[this.height][o.width];
      ExecutorService service = Executors.newWorkStealingPool();
      ArrayList<Future> futureTasks = new ArrayList<>();

      for(int i = 0; i < this.height; i++){
          final int i1 = i;
          futureTasks.add(service.submit(()->{
              for(int j = 0; j < o.width; j++){
                  resMatr[i1][j] = 0;
                  for(int k = 0; k < this.width; k++){
                      resMatr[i1][j] += this.matr[i1][k] * o.matr[k][j];
                  }
              }
          }));
      }
      for(Future task: futureTasks){
          task.get();
      }
      service.shutdown();
      return new DenseMatrix(resMatr);
  }

  @Override public Matrix dmul(Matrix o) throws Exception
  {
      if(o instanceof DenseMatrix){
          return this.dmul((DenseMatrix) o);
      }
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
      else if(o instanceof SparseMatrix){
          return this.denseToSparse().equals((SparseMatrix) o);
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
