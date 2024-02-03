import java.util.ArrayList;

public class Polynomials {
    ArrayList<Integer> polynomial1 = new ArrayList<Integer>();
    ArrayList<Integer> polynomial2 = new ArrayList<Integer>();
    ArrayList<Integer> part1Polynomial1 = new ArrayList<>();
    ArrayList<Integer> part1Polynomial2 = new ArrayList<>();
    ArrayList<Integer> part2Polynomial1 = new ArrayList<>();
    ArrayList<Integer> part2Polynomial2 = new ArrayList<>();
    Integer size;

    public Polynomials(){
        this.size = 2 * getRandomInt(1000, 500);
    }

    public int getRandomInt(int max, int min) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public ArrayList<Integer> generatePolynomial(ArrayList<Integer> list, int size){
        for (int i = 0; i < size; i++){
            list.add(getRandomInt(10, 0));
        }

        return list;
    }

    public void splitPolynomials(){
        this.part1Polynomial1 = new ArrayList<>(this.polynomial1.subList(0, this.size / 2));
        this.part1Polynomial2 = new ArrayList<>(this.polynomial1.subList(this.size / 2, this.polynomial1.size()));
        this.part2Polynomial1 = new ArrayList<>(this.polynomial2.subList(0, this.size / 2));
        this.part2Polynomial2 = new ArrayList<>(this.polynomial2.subList(this.size / 2, this.polynomial1.size()));
    }
}
