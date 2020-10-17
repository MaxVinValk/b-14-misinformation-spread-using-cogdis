import pandas as pd
from matplotlib import pyplot as plt
from statistics import mean

def plotBeliefConvergence(dataFrame, alg):
    """
    Plots individual belief trajectories over epochs.

    Keyword arguments:
    dataFrame -- the data frame from which to collect the data
    alg       -- the algorithm that was used as recommendation strategy
    """
    nodeIDs = set(dataFrame["nodeID"])
    for ID in nodeIDs:
        belief = dataFrame["belief"][dataFrame["nodeID"] == ID]
        col = "blue" if mean(belief[len(belief)//2:]) <= 0.5 else "red"
        plt.plot(range(len(belief)), belief, color = col)
    plt.title("Development of individual belief states over time for algorithm: " + alg)
    plt.ylabel("Belief")
    plt.xlabel("Epoch")
    plt.savefig("DMAS/data_processing/img/" + alg + "/" + "individual_beliefs" + ".png")
    plt.close()

def plotAverageOverEpochs(dataFrame, par, alg):
    """
    Plots average par per epoch for all epochs.

    Keyword arguments:
    dataFrame -- the data frame from which to collect the data
    par       -- the parameter for which the average should be calculated
    alg       -- the algorithm that was used as recommendation strategy 
    """
    dt = dataFrame.groupby(["epoch"]).agg(

            aggPar = pd.NamedAgg(column=par, aggfunc = "mean")

                                                )
    
    plt.plot(range(len(dt)),dt, color="black")
    plt.title("Development of " + par + " average over time for algorithm: " + alg)
    plt.ylabel("Average " + par)
    plt.xlabel("Epoch")
    plt.savefig("DMAS/data_processing/img/" + alg + "/" + par + ".png")
    plt.close()

def plotHistogramsForEpoch(dataframe, par, timePoints, alg, rows, cols):
    """
    Plots histogram for distribution of a selected parameter for
    selection of time points.

    Keyword arguments:
    dataFrame   -- the data frame from which to collect the data
    par         -- the parameter for which the histogram should be calculated
    timePoints  -- the timepoints for which to create the histogram
    alg         -- the algorithm that was used as recommendation 
    rows        -- rows of sub plot figure
    cols        -- cols of sub plot figure
    """
    if rows*cols != len(timePoints):
        raise Exception("The dimensions of the sub-plots must match the number of time points")

    row = 0
    col = 0
    fig, axes = plt.subplots(nrows=rows, ncols=cols)
    plt.subplots_adjust(
            hspace=0.55,
            wspace=0.55
        )

    for tp in timePoints:
        beliefs = dataframe[par][dataframe["epoch"] == tp]
        axes[row, col].hist(beliefs)
        axes[row, col].set_title("Epoch: " + str(tp))
        col += 1
        if col > (cols - 1):
            col = 0
            row += 1

    plt.savefig("DMAS/data_processing/img/" + alg + "/histogram_" + par + ".png")
    plt.close()

if __name__ == "__main__":
    # Read data into pd dataframe

    # Old dataframes that were available online do not work at the moment since they are being replaced.

    #dataFrameRandom = pd.read_csv("https://onedrive.live.com/download?cid=E556A05E08C89AB7&resid=E556A05E08C89AB7%211047920&authkey=ABgnRxegPx4RAMY", sep=",", header=0)
    #dataFramePolarize = pd.read_csv("https://onedrive.live.com/download?cid=E556A05E08C89AB7&resid=E556A05E08C89AB7%211047921&authkey=APTLP1ZYXNT7IxM", sep=",", header=0)

    dataFrameRandom = pd.read_csv("data_out/10_17_16_40-53/data_randomize.csv", sep=",", header=0)
    dataFramePolarize = pd.read_csv("data_out/10_17_16_36-07/data_polarize.csv", sep=",", header=0)
    dataFrameNeutralize = pd.read_csv("data_out/10_17_16_45-48/data_neutralize.csv", sep=",", header=0)

    # Plot belief histograms
    plotHistogramsForEpoch(dataFrameRandom,"belief",[1,1000,2000,3000,4000,5000,6000,7000,8000,9000],"random",2,5)
    plotHistogramsForEpoch(dataFramePolarize,"belief",[1,1000,2000,3000,4000,5000,6000,7000,8000,9000],"polarize",2,5)
    plotHistogramsForEpoch(dataFrameNeutralize,"belief",[1,1000,2000,3000,4000,5000,6000,7000,8000,9000],"neutralize",2,5)

    # Plot weighted openness histograms
    plotHistogramsForEpoch(dataFrameRandom,"weightedOpenness",[1,1000,2000,3000,4000,5000,6000,7000,8000,9000],"random",2,5)
    plotHistogramsForEpoch(dataFramePolarize,"weightedOpenness",[1,1000,2000,3000,4000,5000,6000,7000,8000,9000],"polarize",2,5)
    plotHistogramsForEpoch(dataFrameNeutralize,"weightedOpenness",[1,1000,2000,3000,4000,5000,6000,7000,8000,9000],"neutralize",2,5)
    
    # Plot trajectory of individual beliefs over epochs to show convergence
    plotBeliefConvergence(dataFrameRandom, "random")
    plotBeliefConvergence(dataFramePolarize, "polarize")
    plotBeliefConvergence(dataFrameNeutralize, "neutralize")

    # List all stats you want to average over for both the random and polarized dataset
    avgPars = ["dissonance", "numNeighbours", "avgNeighbourBelief",
               "numConfidants","avgConfidantBelief","numberOfContacts",
               "numberOfConflicts"]

    # Iterate over parameters and save the figures in the respective folders.
    for par in avgPars:
        print("Processing: " + par + " (No worries I am still doing my job :) )")
        plotAverageOverEpochs(dataFrameRandom, par, "random")
        plotAverageOverEpochs(dataFramePolarize, par, "polarize")
        plotAverageOverEpochs(dataFrameNeutralize, par, "neutralize")
    