package HelperUtils

import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
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
/*
  DataCenter Configuration is obtained based on the scheduler Model
 */
class DataCenterConfig (schedulerModel: String) {
  val config = ConfigFactory.load(schedulerModel: String)
  val numberOfHosts = config.getInt("datacenter.numOfHosts")
  val numOfCloudlets = config.getInt("datacenter.numofCloudlets")
  val numOfVms = config.getInt("datacenter.numOfVms")
  val arch = config.getString("datacenter.arch")
  val os = config.getString("datacenter.os")
  val vmm = config.getString("datacenter.vmm")
  val costSec = config.getDouble("datacenter.costSec")
  val costBw = config.getDouble("datacenter.costBw")
  val costMem = config.getDouble("datacenter.costMem")
  val costStorage = config.getDouble("datacenter.costStorage")
}
