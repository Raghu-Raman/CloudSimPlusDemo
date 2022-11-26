import HelperUtils.CreateLogger
import Policies.Scheduling
import Policies.{CloudModels, VmAllocation}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org .cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyRoundRobin

object Simulation :
  val logger = CreateLogger(classOf[Simulation])

  @main
  def runSimulation =
    logger.info("Starting execution of cloud simulations...")

    logger.info("Starting VM and Cloudlet scheduling policies simulations...")

    logger.info("Starting Space Shared scheduling cloud model simulation...")
    val spaceShared = new Scheduling("SpaceShared", new VmSchedulerSpaceShared(), new CloudletSchedulerSpaceShared())
    spaceShared.start()

    logger.info("Starting Time Shared scheduling cloud model simulation...")
    val timeShared = new Scheduling("TimeShared", new VmSchedulerTimeShared(), new CloudletSchedulerTimeShared())
    timeShared.start()

    logger.info("Starting simulation for VM Allocation Policy Round Robin...")
    val roundRobin = new VmAllocation("RoundRobin", vmAllocation = new VmAllocationPolicyRoundRobin())
    roundRobin.start()

    logger.info("Starting cloud models simulations...")
    val cloudModels = new CloudModels()
    cloudModels.start()

    logger.info("Finished cloud simulations...")

class Simulation