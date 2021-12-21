package edu.spbu.matrix;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Разреженная матрица
 */
public class SparseMatrix implements Matrix // хранение матрицы в виде представления CSR
{
  public int height;
  public int width;
  public ArrayList<Double> notZeroValues;
  public ArrayList<Integer> colsIndex;
  public ArrayList<Integer> rowsIndexation; // rowsIndexation[i] = количество ненулевых элементов в строках 1,...,i-1

  public SparseMatrix(int height, int width, ArrayList<Double> notZeroValues,ArrayList<Integer> colsIndex, ArrayList<Integer> rowsIndexation){
    this.height = height;
    this.width = width;
    this.notZeroValues = notZeroValues;
    this.colsIndex = colsIndex;
    this.rowsIndexation = rowsIndexation;
  }
  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public SparseMatrix(String fileName) {
    try {
      Scanner scanner = new Scanner(new FileReader(fileName));
      if (!scanner.hasNextLine())
        throw new Exception("Пустой файл\n");
      this.notZeroValues = new ArrayList<>();
      this.colsIndex = new ArrayList<>();
      this.rowsIndexation = new ArrayList<>();
      this.width = 1;
      this.height = 1;

      String firstLine = scanner.nextLine();
      for(int i = 0; i < firstLine.length(); i++) {
        if (firstLine.charAt(i) == ' ')
          this.width++;
      }
      //this.width--;
      while(scanner.hasNextLine()){
        scanner.nextLine();
        this.height++;
      }

      scanner.reset();
      scanner = new Scanner(new FileReader(fileName));
      scanner = scanner.useLocale(Locale.ENGLISH);
      this.rowsIndexation.add(0); // первый элемент этого массива всегда 0
      double current = 0;
      for(int i = 0; i < this.height; i++){
        for(int j = 0; j < this.width; j++){
          current = scanner.nextDouble();
          if(current != 0){
            this.notZeroValues.add(current);
            this.colsIndex.add(j);
          }
        }
        this.rowsIndexation.add(notZeroValues.size());
      }
    }
    catch (Exception exception){
      exception.printStackTrace();
    }
  }
  /**
   * транспонирование разреженной матрицы
   */
  public SparseMatrix CRS_transposition(){
    int t_height = this.width;
    int t_width = this.height;
    ArrayList<Double> t_nZV = new ArrayList<>(this.notZeroValues.size());
    ArrayList<Integer> t_cI = new ArrayList<>(this.colsIndex.size());
    ArrayList<Integer> t_rI = new ArrayList<>(t_height+1);

    ArrayList<ArrayList<Double>> doubleVectors = new ArrayList<>(this.width);
    ArrayList<ArrayList<Integer>> intVectors = new ArrayList<>(this.width);
    for(int i = 0; i < this.width; i++){
      doubleVectors.add(new ArrayList<>());
      intVectors.add(new ArrayList<>());
    }
    int index = 0;
    for(int i = 0; i < this.height; i++){
      for(int j = 0; j < (this.rowsIndexation.get(i+1) - this.rowsIndexation.get(i)); j++){
        doubleVectors.get(this.colsIndex.get(index)).add(notZeroValues.get(index));
        intVectors.get(this.colsIndex.get(index)).add(i);
        index++;
      }
    }
    t_rI.add(0);
    for(int i = 0; i < t_height; i++) {
      t_nZV.addAll(doubleVectors.get(i));
      t_cI.addAll(intVectors.get(i));
      t_rI.add(t_rI.get(i) + doubleVectors.get(i).size());
    }

    return new SparseMatrix(t_height,t_width,t_nZV,t_cI,t_rI);
  }

  /**
   * однопоточное умножение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  private SparseMatrix mul(SparseMatrix o) throws Exception{
    if(this.width != o.height)
      throw new Exception("Матрицы не могут быть умножены: несовпадение размеров");
    SparseMatrix o_tr = o.CRS_transposition();
    ArrayList<Double> res_nZV = new ArrayList<>();
    ArrayList<Integer> res_cI = new ArrayList<>();
    ArrayList<Integer> res_rI = new ArrayList<>();
    res_rI.add(0);

    for(int i = 0; i < this.height; i++){
      int[] x = new int[this.width]; // массив с индексами ненулевых элементов строки первой матрицы, они имеют индексы равные номеру своего столбца
      for(int k = 0; k < this.width; k++)
        x[k] = -1;
      for(int k = this.rowsIndexation.get(i); k < this.rowsIndexation.get(i+1); k++)
        x[this.colsIndex.get(k)] = k;
      for(int j = 0; j < o_tr.height; j++){
        // умножение i-й строки первой матрицы на j-й столбец второй (или на j-ю строчку транспонированной второй)
        // фактически операция внутри этих циклов заключается в скалярном умножении двух разреженных векторов
        double scalarProduct = 0;
        for(int k = o_tr.rowsIndexation.get(j); k < o_tr.rowsIndexation.get(j+1); k++){
          if(x[o_tr.colsIndex.get(k)] != -1)
            scalarProduct += this.notZeroValues.get(x[o_tr.colsIndex.get(k)]) * o_tr.notZeroValues.get(k);
        }
        if(scalarProduct != 0){
          res_nZV.add(scalarProduct);
          res_cI.add(j);
        }
      }
      res_rI.add(res_nZV.size());
    }

    return new SparseMatrix(this.height, o.width, res_nZV, res_cI, res_rI);
  }

  private SparseMatrix mul(DenseMatrix o) throws Exception{
    if(this.width != o.height)
      throw new Exception("Матрицы не могут быть умножены: несовпадение размеров");
    ArrayList<Double> res_nZV = new ArrayList<>();
    ArrayList<Integer> res_cI = new ArrayList<>();
    ArrayList<Integer> res_rI = new ArrayList<>();
    res_rI.add(0);

    for(int i = 0; i < this.height; i++){
      for(int j = 0; j < o.width; j++){
        double scalarProduct = 0;
        for(int k = this.rowsIndexation.get(i); k < this.rowsIndexation.get(i+1); k++){
          scalarProduct += this.notZeroValues.get(k) * o.matr[this.colsIndex.get(k)][j];
        }
        if(scalarProduct != 0){
          res_nZV.add(scalarProduct);
          res_cI.add(j);
        }
      }
      res_rI.add(res_nZV.size());
    }

    return new SparseMatrix(this.height, o.width, res_nZV, res_cI, res_rI);
  }

  @Override public Matrix mul(Matrix o) throws Exception
  {
    if(o instanceof SparseMatrix)
      return this.mul((SparseMatrix) o);
    else if(o instanceof DenseMatrix)
      return this.mul((DenseMatrix) o);
    return null;
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  private SparseMatrix dmul(SparseMatrix o) throws Exception{
    if(this.width != o.height)
      throw new Exception("Matrices cannot be multiplied because of a size difference\n");

    ExecutorService service = Executors.newWorkStealingPool();
    Future[] futureTasks = new Future[this.height];
    SparseMatrix[] submatrices = new SparseMatrix[this.height];
    //for(int i = 0; i < this.height; i++) submatrices[i] = new DenseMatrix(1, this.width).denseToSparse();
    //получение каждой строчки и ее умножение на вторую матрицу
    for(int i = 0; i < this.height; i++){
      final int i1 = i;
      futureTasks[i1] = service.submit(()->{
        ArrayList<Double> sub_nZV = new ArrayList<>();
        ArrayList<Integer> sub_cI = new ArrayList<>();
        ArrayList<Integer> sub_rI = new ArrayList<>(2);
        //нужно из листов самой матрицы скопировать отрезки, находящиеся между индексами this.rowsIndexation.get(i1) и ...get(i1+1)
        sub_rI.add(0);
        sub_rI.add(this.rowsIndexation.get(i1 + 1) - this.rowsIndexation.get(i1));//количество ненулевых в сабматрице, поскольку она 1строчная
        sub_nZV.addAll(this.notZeroValues.subList(this.rowsIndexation.get(i1), this.rowsIndexation.get(i1+1)));
        sub_cI.addAll(this.colsIndex.subList(this.rowsIndexation.get(i1), this.rowsIndexation.get(i1+1)));
        submatrices[i1] = new SparseMatrix(1, this.width, sub_nZV, sub_cI, sub_rI);
        try {
          submatrices[i1] = submatrices[i1].mul(o);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
    for(Future task: futureTasks){
      task.get();
    }
    service.shutdown();

    //соединение матрицы воедино
    ArrayList<Double> res_nZV = new ArrayList<>();
    ArrayList<Integer> res_cI = new ArrayList<>();
    ArrayList<Integer> res_rI = new ArrayList<>(this.height + 1);
    res_rI.add(0);
    for(int i = 0; i < this.height; i++){
      res_nZV.addAll(submatrices[i].notZeroValues);
      res_cI.addAll(submatrices[i].colsIndex);
      res_rI.add(res_nZV.size());
    }
    return new SparseMatrix(this.height, o.width, res_nZV, res_cI, res_rI);
  }

  private SparseMatrix dmul(DenseMatrix o) throws Exception{
    if(this.width != o.height)
      throw new Exception("Matrices cannot be multiplied because of a size difference\n");

    ExecutorService service = Executors.newWorkStealingPool();
    Future[] futureTasks = new Future[this.height];
    SparseMatrix[] submatrices = new SparseMatrix[this.height];
    //for(int i = 0; i < this.height; i++) submatrices[i] = new DenseMatrix(1, this.width).denseToSparse();
    //получение каждой строчки и ее умножение на вторую матрицу
    for(int i = 0; i < this.height; i++){
      final int i1 = i;
      futureTasks[i1] = service.submit(()->{
        ArrayList<Double> sub_nZV = new ArrayList<>();
        ArrayList<Integer> sub_cI = new ArrayList<>();
        ArrayList<Integer> sub_rI = new ArrayList<>(2);
        //нужно из листов самой матрицы скопировать отрезки, находящиеся между индексами this.rowsIndexation.get(i1) и ...get(i1+1)
        sub_rI.add(0);
        sub_rI.add(this.rowsIndexation.get(i1 + 1) - this.rowsIndexation.get(i1));//количество ненулевых в сабматрице, поскольку она 1строчная
        sub_nZV.addAll(this.notZeroValues.subList(this.rowsIndexation.get(i1), this.rowsIndexation.get(i1+1)));
        sub_cI.addAll(this.colsIndex.subList(this.rowsIndexation.get(i1), this.rowsIndexation.get(i1+1)));
        submatrices[i1] = new SparseMatrix(1, this.width, sub_nZV, sub_cI, sub_rI);
        try {
          submatrices[i1] = submatrices[i1].mul(o);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
    for(Future task: futureTasks){
      task.get();
    }
    service.shutdown();

    //соединение матрицы воедино
    ArrayList<Double> res_nZV = new ArrayList<>();
    ArrayList<Integer> res_cI = new ArrayList<>();
    ArrayList<Integer> res_rI = new ArrayList<>(this.height + 1);
    res_rI.add(0);
    for(int i = 0; i < this.height; i++){
      res_nZV.addAll(submatrices[i].notZeroValues);
      res_cI.addAll(submatrices[i].colsIndex);
      res_rI.add(res_nZV.size());
    }
    return new SparseMatrix(this.height, o.width, res_nZV, res_cI, res_rI);
  }

  @Override public Matrix dmul(Matrix o) throws Exception
  {
    if(o instanceof SparseMatrix)
      return this.dmul((SparseMatrix) o);
    if(o instanceof DenseMatrix)
      return this.dmul((DenseMatrix) o);
    return null;
  }

  /**
   * сравнивает с обоими вариантами
   * @param o
   * @return
   */
  public boolean equals(SparseMatrix o){
    if(this == o)
      return true;
    if(this.width != o.width || this.height != o.height)
      return false;
    if(!(this.notZeroValues.equals(o.notZeroValues)))
      return false;
    if(!(this.colsIndex.equals(o.colsIndex)))
      return false;
    return this.rowsIndexation.equals(o.rowsIndexation);
  }

  @Override public boolean equals(Object o) {
    if(o instanceof SparseMatrix)
      return this.equals((SparseMatrix) o);
    else if(o instanceof DenseMatrix)
      return this.equals(((DenseMatrix) o).denseToSparse());
    return false;
  }

  public String toString(){
    String result = "";
    int index = 0;
    for(int i = 0; i < this.height; i++){
      int k = this.rowsIndexation.get(i + 1) - this.rowsIndexation.get(i);
      for(int j = 0; j < this.width; j++){
        if((index < k) && (this.colsIndex.get(index) == j)) {
          result = result.concat(Double.toString(this.notZeroValues.get(index)));
          result = result.concat(" ");
          index++;
        }
        else {
          result = result.concat(Double.toString(0));
          result = result.concat(" ");
        }
      }
      result = result.substring(0, result.length() - 1);
      result = result.concat("\n");
    }
    return result;
  }

  public int hashCode() {
    int result = Arrays.hashCode(this.notZeroValues.toArray());
    result += this.height;
    result *= 31;
    result += Arrays.hashCode(this.colsIndex.toArray());
    return result;
  }
}

