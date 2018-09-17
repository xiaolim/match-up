import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.lang.*;

public class Randomizer{

  public static void main(String[] args){
    int sum = 0;
    List<Integer> selection = trueRandom(1,11,90,15);
    for(int i=0; i<selection.size(); i++){
      sum += selection.get(i);
    }
      System.out.println("The sum is: " + sum);
  }


  public static List<Integer> trueRandom(int min, int max, int sum, int num){
    Random rand = new Random();
		int desired_sum = sum;
		int current_sum = 0;
		int remaining = desired_sum - current_sum;
		int current_min = min;
		int current_max = max;
		int num_players = num;
		int player_skill = 0;
    List<Integer> skills = new ArrayList<Integer>();

    for(int i=0; i<num; i++){
      num_players--;
      if(num_players != 0){
        while (((remaining - current_max)/num_players) <= min){
          current_max -= 1;
        }
        while (((remaining - current_min)/num_players) >= max){
          current_min += 1;
        }

        int range = current_max - current_min;
        System.out.println("The range is: (" + current_min + ", " + current_max + ")");
        if(range == 0){
          player_skill = current_max;
        }
        else{
          player_skill = current_min + rand.nextInt(range);
        }
        skills.add(player_skill);
        current_sum += player_skill;
        remaining = desired_sum - current_sum;
      }
      else{
        player_skill = remaining;
        skills.add(player_skill);
        current_sum += player_skill;
        remaining = desired_sum - current_sum;
      }
      // System.out.println("Selected player with skill level: " + player_skill);
      // System.out.println("The remainder is: " + remaining);
      // System.out.println("Number of players left: " + num_players);
    }
    return skills;
  }
}
