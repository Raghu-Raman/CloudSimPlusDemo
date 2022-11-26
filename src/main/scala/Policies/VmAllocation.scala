package Policies

import HelperUtils.{CreateLogger, DataCenter, Cloudlets, DataCenterConfig, HostClass, VmConfigs}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerAbstract, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerAbstract, VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple

import scala.collection.JavaConverters.*

/* Class to simulate Round Robin.
  The parameters are
    1)  schedulerModel 
    2)  vmScheduler 
    3)  cloudletScheduler 
 */
class VmAllocation(schedulerModel: String, vmAllocation: VmAllocationPolicy)  {

  // Creating a VmLogger instance to log events
  val VmLogger = CreateLogger(classOf[VmAllocation])

  def start() = {
    // Create a cloudsim instance for simulation. Also creates the Cloud Information Service (CIS) entity internally.
    val cloudsim = new CloudSim

    // Create a utility instance to create cloud entities.
    val datacenterutil = new DataCenter(schedulerModel, vmAllocation = vmAllocation: VmAllocationPolicy)

    // Create a datacenter instance
    val datacenter = datacenterutil.createDatacenter(cloudsim)

    //   Create a broker that will try to host customer's VMs at the first Datacenter found. If there isn't capacity in that one, it will try the other ones.
    val broker = new DatacenterBrokerSimple(cloudsim)

    // Create a list of VMs
    VmLogger.info("Initiating VMs")
    val vmList = datacenterutil.createVms()

    // Create a list of cloudlets
    VmLogger.info("Initiating cloudlets")
    val cloudletList = datacenterutil.createCloudlets()

    // Submit VMs and cloudlets to broker
    VmLogger.info("Linking cloudlets, Vm to the broker")
    broker.submitVmList(vmList.asJava)
    broker.submitCloudletList(cloudletList.asJava)

    // Start the simulation
    cloudsim.start()

    // Build the simulation table
    val finishedCloudlet = broker.getCloudletFinishedList()
    CloudletsTableBuilder(finishedCloudlet).build()
    VmLogger.info(s"Finished simulation of $schedulerModel VM ALlocation Policy.  \n\n\n")
  }

}
