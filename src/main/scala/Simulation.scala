import HelperUtils.CreateLogger
import Policies.Scheduling
import Policies.{CloudModels, VmAllocation}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org .cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyRoundRobin
import java.io.File
import java.io.PrintWriter

object Simulation {
  val logger = CreateLogger(classOf[Simulation.type ])

  @main def runSimulation =
    logger.info("Starting the cloud simulations\n")
    logger.info("Simulating different policies\n")
    logger.info("Space Shared scheduling simulation\n")
    val spaceShared = new Scheduling("SpaceShared", new VmSchedulerSpaceShared(), new CloudletSchedulerSpaceShared())
    spaceShared.start()

    logger.info("Time Shared scheduling  simulation\n")
    val timeShared = new Scheduling("TimeShared", new VmSchedulerTimeShared(), new CloudletSchedulerTimeShared())
    timeShared.start()

    logger.info("VM Allocation Policy Round Robin\n")
    val roundRobin = new VmAllocation("RoundRobin", vmAllocation = new VmAllocationPolicyRoundRobin())
    roundRobin.start()

    logger.info("Start the simulations")
    val cloudModels = new CloudModels()
    cloudModels.start()
    logger.info("Completed.\n")
}