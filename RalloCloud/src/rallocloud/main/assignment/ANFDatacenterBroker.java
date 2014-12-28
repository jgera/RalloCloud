/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rallocloud.main.assignment;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * Arbitrary next fit
 *
 * @author Atakan
 */
public class ANFDatacenterBroker extends BrokerStrategy {

    public static int i = 0;

    public ANFDatacenterBroker(String name) throws Exception {
        super(name);
    }

    @Override
    protected void createSingleVm(int vmId) {
        ArrayList<Integer> hs = new ArrayList<>();
        hs.add(vmId);
        createGroupVm(hs, new Double[0][0]);
    }

    @Override
    protected void createGroupVm(List<Integer> g, Double[][] t) {
        for (int vmId : g) {
            Vm vm = VmList.getById(getVmList(), vmId);
            setVmsRequested(getVmsRequested() + 1);
            int datacenterId = datacenterIdsList.get(i++ % datacenterIdsList.size());
            Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vmId + " in " + CloudSim.getEntityName(datacenterId) + " (" + datacenterId + ")");
            sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
        }
    }
}
