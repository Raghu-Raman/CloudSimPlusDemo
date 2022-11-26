package CloudSimulation
import HelperUtils.{DataCenter, Cloudlets, DataCenterConfig, HostClass, VmConfigs}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerTimeShared}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.funspec.AnyFunSpec

import scala.collection.JavaConverters.*

class testCloudSimulation extends AnyFunSpec {
  val schedulerModel: String = "TimeShared"
  val vmScheduler = new VmSchedulerTimeShared()
  val cloudletScheduler = CloudletSchedulerTimeShared()

  
  val cloudsim = new CloudSim

  describe("Cloud Sim Instance") {
    it("not equal to null") {
      assert(cloudsim != null)
    }
  }
  val broker = new DatacenterBrokerSimple(cloudsim)
  describe("Broker Instance") {
    it("not equal to null") {
      assert(broker != null)
    }
  }

    val testConfig = ConfigFactory.load(schedulerModel)
    describe("Config parameters"){
    it("test number of hosts") {
      assert(testConfig.getInt("datacenter.numOfHosts") == 2)
    }
    it("test host ram") {
      assert(testConfig.getInt("host.ramConf") == 10000)
    }
    it("test host bandwidth") {
      assert(testConfig.getInt("host.bandwidth") == 100000)
    }
  }
  
  val datacenterutil = new DataCenter(schedulerModel, vmScheduler: VmScheduler, cloudletScheduler: CloudletScheduler)
  val datacenter = datacenterutil.createDatacenter(cloudsim)
  
  describe("Datacenter Instance") {
    it("not equal to null") {
      assert(datacenter != null)
    }
  }

  val vmList = datacenterutil.createVms()

  describe("VM List") {
    it("not equal to null") {
      assert(vmList != null)
    }
    it("should be greater than zero.") {
      assert(vmList.length > 0)
    }
  }

  val cloudletList = datacenterutil.createCloudlets()

  describe("Cloudlet list") {
    it("not equal to null") {
      assert(cloudletList != null)
    }
    it("should be greater than zero") {
      assert(cloudletList.length > 0)
    }
  }

  broker.submitVmList(vmList.asJava)
  broker.submitCloudletList(cloudletList.asJava)

  cloudsim.start()
  describe("Simulation") {
    it("Not running") {
      assert(cloudsim.isRunning() == false)
    }
  }
}