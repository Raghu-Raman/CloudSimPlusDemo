package Policies

import HelperUtils.{CreateLogger, DataCenter, Cloudlets, DataCenterConfig, HostClass, VmConfigs}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{CloudletSimple, Cloudlet}
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
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import java.util

import scala.collection.JavaConverters.*

/*
Implementing Software as a Service(Saas)
              Platform as a service(Paas)
              Infrastructure as a service(Iaas)
Connecting the corresponding datacenters using brite topology
*/
class CloudModels()  {
  val modelLogger = CreateLogger(classOf[CloudModels])

  def start() = {
    modelLogger.info(s"Starting execution of cloud models simulation.")

    // Create cloudsim instance and Cloud Information Service (CIS) entity.
    val cloudSim = new CloudSim
    // Create a broker instance
    val broker = new DatacenterBrokerSimple(cloudSim)
    //    Creating instance for Saas with utils and data center
    val SaasDataCenterUtils = new DataCenter("Saas")
    val SaasDataCenter = SaasDataCenterUtils.createDatacenter(cloudSim)
    //    Creating instance for Paas with utils and data center
    val PaasDataCenterUtils = new DataCenter("Paas")
    val PaasDataCenter = PaasDataCenterUtils.createDatacenter(cloudSim)
    //    Creating instance for Iaas with utils and data center
    val IaasDataCenterUtils = new DataCenter("Iaas")
    val IaasDataCenter = IaasDataCenterUtils.createDatacenter(cloudSim)
    

    // Initiate the topology.
    val networkTopology = new BriteNetworkTopology("topology.brite")
    cloudSim.setNetworkTopology(networkTopology)
    networkTopology.mapNode(SaasDataCenter, 0)
    networkTopology.mapNode(PaasDataCenter, 1)
    networkTopology.mapNode(IaasDataCenter, 2)
    networkTopology.mapNode(broker, 3)

    // Create the list of VMs for ech datacenter
    modelLogger.info("Initiating VMs")
    val IaasVmList = IaasDataCenterUtils.createVms()
    val PaasVmList = PaasDataCenterUtils.createVms()
    val SaasVmList = SaasDataCenterUtils.createVms()
    val allVmList = SaasVmList ::: PaasVmList ::: IaasVmList 

    // Create a list of cloudlets for all datacenters
    modelLogger.info("Intiating cloudlets")
    val SaasCloudletList = SaasDataCenterUtils.createCloudlets()
    val PaasCloudletList = PaasDataCenterUtils.createCloudlets()
    val IaasCloudletList = IaasDataCenterUtils.createCloudlets()
    val allCloudletList = SaasCloudletList ::: PaasCloudletList ::: IaasCloudletList

    // Submit VMs and cloudlets to broker
    modelLogger.info("Submitting VMs and cloudlets to broker")
    broker.submitVmList(allVmList.asJava)
    broker.submitCloudletList(allCloudletList.asJava)

    // Start the simulation
    cloudSim.start()

    val finishedCloudlet : util.List[Cloudlet] = broker.getCloudletFinishedList()
    CloudletsTableBuilder(finishedCloudlet).build()

    val scalaCloudletList : List[Cloudlet] =  finishedCloudlet.asScala.toList.sorted
    scalaCloudletList.map(cloudlet => {
      val cloudletId = cloudlet.getId
      val cost = cloudlet.getTotalCost()
      val dc = cloudlet.getLastTriedDatacenter()
      modelLogger.info(s"Cost: $dc Cloudlet $cloudletId is $cost")
    }
    )
    modelLogger.info(s"Finished simulation of cloud models simulation.")
  }

}