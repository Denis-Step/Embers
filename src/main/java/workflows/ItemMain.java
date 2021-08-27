package workflows;

import workflows.workflows.ItemWorkflow;
import workflows.workflows.ItemWorkflowImpl;

public class ItemMain {

    public static void main(String[] args) {
        ItemWorkflow itemWorkflow = new ItemWorkflowImpl();
        itemWorkflow.showTransactions();
    }
}
