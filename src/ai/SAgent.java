package ai;

import java.awt.*;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mathias on 18-05-2016.
 */
public class SAgent extends Agent implements Comparator<GoalTask> {

    private SAAStarPlanner planner;
    private boolean newAgentType;
    private ArrayList<GoalTask> sortedGoalTasks = new ArrayList<>();
    private PriorityQueue<GoalTask> goaltaskQueue;
    private SAState prevPlanLastState;
    private LinkedList<SAState> overAllPlan;

    public SAgent(Agent a, boolean newAgentType){
        super(a.getAgentId(), "", new Point(a.getPosition().x,a.getPosition().y));
        this.newAgentType = newAgentType;
        this.overAllPlan = new LinkedList<>();
    }

    @Override
    public int compare(GoalTask o1, GoalTask o2) {
        if(o1.getPreviousGoal() == o2.getGoalId())
            return -1; // o2 > o1 because it should be solved before
        if(o2.getPreviousGoal() == o1.getGoalId())
            return 1;
        if(o1.getPreviousGoal() == -1 && o2.getPreviousGoal() == -1){
            Point o1BoxPos = Supervisor.getInstance().getLevel().getBoxes().get(o1.getBoxId()).location;
            Point o1GoalPos = Supervisor.getInstance().getLevel().getGoalWithId(o1.getGoalId()).point;

            Point o2BoxPos = Supervisor.getInstance().getLevel().getBoxes().get(o2.getBoxId()).location;
            Point o2GoalPos = Supervisor.getInstance().getLevel().getGoalWithId(o2.getGoalId()).point;

            return  0;//(SimpleHeuristic.euclidean(o1BoxPos,o1GoalPos) - SimpleHeuristic.euclidean(o2BoxPos,o2GoalPos)); //If o1
        }
        if(o1.getPreviousGoal() == -1 && o2.getPreviousGoal() != -1){
            return 1;
        }
        if(o1.getPreviousGoal() != -1 && o2.getPreviousGoal() == -1){
            return -1;
        }
        return 0;
    }

    private void setupGoalTasks() {
        PriorityQueue<GoalTask> goalTasks = Supervisor.getInstance().getGoalTasks();
        while (!goalTasks.isEmpty()) {
            this.sortedGoalTasks.add(goalTasks.poll());
        }
        goalTasks.addAll(this.sortedGoalTasks);
    }

    private void preProcessGoalTask(){

        for(GoalTask gt: this.sortedGoalTasks){
            Point goalPos = Supervisor.getInstance().getLevel().getGoals().get(gt.getGoalId()).point;
            gt.setPreviousGoal(Supervisor.getInstance().getLevel().getPrevGoalAt(goalPos.x, goalPos.y));
        }
    }


    @Override
    public void run() {
        //Single agent loop!

        setupGoalTasks();
        preProcessGoalTask();
        goaltaskQueue = new PriorityQueue<>(this);
        goaltaskQueue.addAll(sortedGoalTasks);


        SAState initialState = null;

        while(!goaltaskQueue.isEmpty() || !allGoalsFulfilled()) {

            this.planner = new SAAStarPlanner();

            if(newAgentType){
                ArrayList<GoalTask> singleItemList = new ArrayList<>();
                singleItemList.add(goaltaskQueue.poll());
                initialState = setupInitialStateForGoalTasks(singleItemList, initialState);
            }else{
                initialState = setupInitialStateForGoalTasks(sortedGoalTasks, initialState);
            }
            LinkedList<SAState> states = new LinkedList<>();
            states = planner.generatePlan(initialState);

            System.err.println("Single-Agent found plan of length:" + states.size());

            // Just printing the plans actions
            System.err.print(this + " My plan: ");
            for (SAState state : states) {
                System.err.print(state.action + " ");
            }

            overAllPlan.addAll(states);

            if(!newAgentType){
                goaltaskQueue.clear(); //We should have solved all tasks by now!
            }

            initialState = states.getLast();
            updateResources(initialState);

            if(goaltaskQueue.isEmpty() && !allGoalsFulfilled()){
                makeNewGoalTasks();
            }
        }

        System.err.println("Sending solution to server!");
        System.err.println("Solution length:" + overAllPlan.size());

        for (SAState state : overAllPlan) {
            System.out.println(state.action.toActionString());
            //System.out.flush();
            try {
                String response = Supervisor.getInstance().getServerMessages().readLine();
                System.err.print("Response: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeNewGoalTasks() {
        for(GoalTask gt: sortedGoalTasks){
            Box b = Supervisor.getInstance().getLevel().getBoxWithId(gt.getBoxId());
            Goal g = Supervisor.getInstance().getLevel().getGoalWithId(gt.getGoalId());
            if(!b.location.equals(g.point)){
                goaltaskQueue.add(gt);
            }
        }
    }

    private void updateResources(SAState state) {
        for (Map.Entry<Integer, Point> entry : state.getHeuristicBoxes().entrySet()) {
            Box b = Supervisor.getInstance().getLevel().getBoxWithId(entry.getKey());
            b.location.x = entry.getValue().x;
            b.location.y = entry.getValue().y;
        }
    }

    private boolean allGoalsFulfilled() {
        for(GoalTask gt: sortedGoalTasks){
            Box b = Supervisor.getInstance().getLevel().getBoxWithId(gt.getBoxId());
            Goal g = Supervisor.getInstance().getLevel().getGoalWithId(gt.getGoalId());
            if(!b.location.equals(g.point))
                return false;
        }
        return true;
    }

    private SAState setupInitialStateForGoalTasks(ArrayList<GoalTask> goaltasks, SAState parent) {
        SAState initialState = new SAState(null);
        initialState.goalTasks = goaltasks;

        initialState.boxes = new char[SAState.MAX_ROW][SAState.MAX_COLUMN];

        if(parent == null){

            Point agentPos = Supervisor.getInstance().getSingleAgent().getPosition();
            initialState.agentCol = agentPos.x;
            initialState.agentRow = agentPos.y;

            HashMap<Integer,Point> boxesPos = new HashMap<>();
            for(Box b: Supervisor.getInstance().getLevel().getBoxes().values()){
                //Box b = Supervisor.getInstance().getLevel().getBoxWithId(gt.getBoxId());
                initialState.boxes[b.location.y][b.location.x] = b.letter;
                boxesPos.put(b.id,new Point(b.location));
            }

            initialState.setHeuristicBoxes(boxesPos);
        }else{
            initialState.agentCol = parent.agentCol;
            initialState.agentRow = parent.agentRow;

            initialState.boxes = parent.boxes;

            HashMap<Integer,Point> boxesPosParent = new HashMap<>();
            for(GoalTask gt: sortedGoalTasks){
                Point b = parent.getBoxWithIdPos(gt.getBoxId());
                boxesPosParent.put(gt.getBoxId(),new Point(b));
            }

            initialState.setHeuristicBoxes(boxesPosParent);

        }

        return initialState;
    }


}
