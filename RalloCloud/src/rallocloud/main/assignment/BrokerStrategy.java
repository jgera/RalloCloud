/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rallocloud.main.assignment;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;
import rallocloud.main.Statistician;

/**
 *
 * @author Atakan
 */
public class BrokerStrategy extends org.cloudbus.cloudsim.DatacenterBroker{
    
    public static int i;
    
    public BrokerStrategy(String name) throws Exception {
        super(name);
    }
    
    @Override
    protected void processVmCreate(SimEvent ev) { //For statistician class
        int[] data = (int[]) ev.getData();
        int datacenterId = data[0];
        int vmId = data[1];
        int result = data[2];
        Statistician.trial();
        if (result == CloudSimTags.TRUE) {
                getVmsToDatacentersMap().put(vmId, datacenterId);
                getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
                Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vmId
                                + " has been created in Datacenter #" + datacenterId + ", Host #"
                                + VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
        } else {
                Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId
                                + " failed in Datacenter #" + datacenterId);
                Statistician.rejected();
        }

        incrementVmsAcks();

        // all the requested VMs have been created
        if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
                submitCloudlets();
        } else {
                // all the acks received, but some VMs were not created
            if (getVmsRequested() == getVmsAcks()) {
                // find id of the next datacenter that has not been tried
                for (int nextDatacenterId : getDatacenterIdsList()) {
                        if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
                                createVmsInDatacenter(nextDatacenterId);
                                return;
                        }
                }

                // all datacenters already queried
                if (getVmsCreatedList().size() > 0) { // if some vm were created
                        submitCloudlets();
                } else { // no vms created. abort
                        Log.printLine(CloudSim.clock() + ": " + getName()
                                        + ": none of the required VMs could be created. Aborting");
                        finishExecution();
                }
            }
        }
    }
}