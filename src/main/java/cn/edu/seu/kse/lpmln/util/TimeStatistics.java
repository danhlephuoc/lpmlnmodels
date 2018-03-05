package cn.edu.seu.kse.lpmln.util;

/**
 * @author 许鸿翔
 * @date 2018/3/2
 */
public class TimeStatistics {
    public TimeCounter totalTime;
    public TimeCounter solveTime;
    public TimeCounter parallelTime;

    public TimeStatistics(){
        totalTime = new TimeCounter();
        solveTime = new TimeCounter();
        parallelTime = new TimeCounter();
    }

    public void printTime(){
        System.out.println("totalTime:"+totalTime.time);
        System.out.println("solveTime:"+solveTime.time);
        System.out.println("parallelTime:"+parallelTime.time);
    }

    public void printTime(String prefix){
        System.out.println(prefix);
        printTime();
    }
}
