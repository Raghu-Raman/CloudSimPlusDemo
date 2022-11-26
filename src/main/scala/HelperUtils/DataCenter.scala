package HelperUtils

import Policies.Scheduling
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
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
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull

import scala.collection.JavaConverters.*

class DataCenter (schedulerModel:String, vmScheduler: VmScheduler = new VmSchedulerSpaceShared(), cloudletScheduler: CloudletScheduler = new CloudletSchedulerTimeShared(), vmAllocation: VmAllocationPolicy = new VmAllocationPolicySimple()) {


  // Logger  is created for datacenter
  val dataCenterLogger = CreateLogger(classOf[DataCenter])

  // Configuration
  dataCenterLogger.info(s"Parsing configurations from $schedulerModel.conf")
  val datacenterConfig = new DataCenterConfig(schedulerModel)//Scheduler Model passed as parameter is given as input from the DataCenterConfig file
  val hostConfig = new HostClass(schedulerModel)// Host Configuration is configured from the HostClass file.
  val vmConfig = new VmConfigs(schedulerModel) // VM Configuration is obtained from VMConfigs.

  // Configuration of cloudlets to be assigned to VMs to be created
  val cloudletConfig = new Cloudlets(schedulerModel)

  dataCenterLogger.info(s"Configuration parsing completed from $schedulerModel.conf.")

  val numberOfHosts = datacenterConfig.numberOfHosts
  val numOfVms = datacenterConfig.numOfVms
  val numofCloudlets = datacenterConfig.numOfCloudlets

  dataCenterLogger.info("Datacenter configuration are as:")
  dataCenterLogger.info(s"Number of hosts: $numberOfHosts")
  dataCenterLogger.info(s"Number of VMs: $numOfVms")
  dataCenterLogger.info(s"Number of cloudlets: $numofCloudlets")

  /*  The following function creates a datacenter.
   Parameters are configured hosts, Allocation Policy, Architecture, OS, Cost
  */
  def createDatacenter(cloudsim: CloudSim) : Datacenter = {
    val hostList = createHost(datacenterConfig.numberOfHosts)
    val dc = new DatacenterSimple(cloudsim, hostList.asJava, vmAllocation)
    dc.getCharacteristics().setArchitecture(datacenterConfig.arch).setOs(datacenterConfig.os).setCostPerBw(datacenterConfig.costBw).setCostPerStorage(datacenterConfig.costStorage).setCostPerMem(datacenterConfig.costMem)
    return dc
  }
  
  /* The following function creates a list of hosts.
  The ram , BandWidth and Storage are passed as parameters.
 */
  def createHost(numberOfHosts: Int) = {
    val peList : List[Pe] = 1.to(hostConfig.numberOfPes).map(x=>new PeSimple(hostConfig.mipsConf)).toList
    1.to(numberOfHosts).map(x=>new HostSimple(hostConfig.ramConf, hostConfig.bandwidth, hostConfig.storage, peList.asJava, true).setVmScheduler(vmScheduler.getClass().getDeclaredConstructor().newInstance())).toList
  }
    /*
    The following function creates a list of VMs.
    MIPS Capacity, PEs, RAM, BandWidth are passed as parameters.
    */
  def createVms() = {
    val numOfVms = datacenterConfig.numOfVms
    1.to(numOfVms).map(x =>
      new VmSimple(vmConfig.mipsConf, vmConfig.PesConf, cloudletScheduler.getClass().getDeclaredConstructor().newInstance()).setRam(vmConfig.ramConf).setBw(vmConfig.bandwidth).setSize(vmConfig.size)
    ).toList
  }

  /*
   The following function creates a list of cloudlets.
   The cloud lets are mapped with length, PEs and Utilization Model
   */
  def createCloudlets() = {
    val numOfCloudlets = datacenterConfig.numOfCloudlets
    val utilizationModel = cloudletConfig.utilizationModel
    1.to(numOfCloudlets).map(x=>new CloudletSimple(cloudletConfig.lengthCloudlet, cloudletConfig.numOfPes, utilizationModel).setSizes(cloudletConfig.size)).toList
  }
}
