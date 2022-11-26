package Policies

import HelperUtils.{CreateLogger, DataCenter, Cloudlets, DataCenterConfig, HostClass, VmConfigs}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
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

/* A class to simulate Time shared and Space shared VM and Cloudlet scheduling policies.
The parameters are
  1)  schedulerModel 
  2)  vmScheduler 
  3)  cloudletScheduler 
 */
class Scheduling(schedulerModel: String, vmScheduler: VmScheduler, cloudletScheduler: CloudletScheduler)  {

  // Creating a schedulingLogger instance to log events
  val schedulingLogger = CreateLogger(classOf[Scheduling])

  def start() = {
    // Create a cloud sim instance and also creates cloud information service
    val cloudsim = new CloudSim

    // Create a utility instance to create cloud entities.
    val datacenterutil = new DataCenter(schedulerModel, vmScheduler: VmScheduler, cloudletScheduler: CloudletScheduler)

    // Create a datacenter 
    val datacenter = datacenterutil.createDatacenter(cloudsim)

//   Create a broker that will try to host customer's VMs at the first Datacenter found. If there isn't capacity in that one, it will try the other ones.
    val broker = new DatacenterBrokerSimple(cloudsim)


    // Create a list of VMs
    schedulingLogger.info("Initiating VMs")
    val vmList = datacenterutil.createVms()

    // Create a list of cloudlets
    schedulingLogger.info("Initiating cloudlets")
    val cloudletList = datacenterutil.createCloudlets()


    // Submit VMs and cloudlets to broker
    schedulingLogger.info("Linking VMs, cloudlets to the broker")
    broker.submitVmList(vmList.asJava)
    broker.submitCloudletList(cloudletList.asJava)

    // Start the simulation
    cloudsim.start()

    // Build the simulation table
    val finishedCloudlet = broker.getCloudletFinishedList()
    CloudletsTableBuilder(finishedCloudlet).build()

    val scalaCloudletList : List[Cloudlet] =  finishedCloudlet.asScala.toList
    scalaCloudletList.map(cloudlet => {
      val cloudletId = cloudlet.getId
      val cost = cloudlet.getTotalCost()
      val dc = cloudlet.getLastTriedDatacenter()
      schedulingLogger.info(s"Cost: $dc Cloudlet $cloudletId is $cost")
    }
    )
    schedulingLogger.info(s"Finished simulation .")

    schedulingLogger.info(s"Completed simulation of the $schedulerModel VM and cloudlet scheduling policy. \n\n\n")
  }

}
