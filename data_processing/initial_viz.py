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

def plotHistogramsForEpoch(dataframe, par, timepoints, alg, rows, cols, h_space = 0.55, w_space = 0.55):
    """
    Plots histogram for distribution of a selected parameter for
    selection of time points.

    Keyword arguments:
    dataframe   -- the data frame from which to collect the data
    par         -- the parameter for which the histogram should be calculated
    timepoints  -- the timepoints for which to create the histogram
    alg         -- the algorithm that was used as recommendation 
    rows        -- rows of sub plot figure
    cols        -- cols of sub plot figure
    """
    if rows*cols != len(timepoints):
        raise Exception("The dimensions of the sub-plots must match the number of time points")

    row = 0
    col = 0
    fig, axes = plt.subplots(nrows=rows, ncols=cols, sharey=True)
    plt.subplots_adjust(
            hspace=h_space,
            wspace=w_space
        )

    for tp in timepoints:
        dt = dataframe[par][dataframe["epoch"] == tp]
        axes[row, col].hist(dt)
        axes[row, col].set_title("Epoch: " + str(tp))
        col += 1
        if col > (cols - 1):
            col = 0
            row += 1

    plt.savefig("DMAS/data_processing/img/" + alg + "/histogram_" + par + ".png")
    plt.close()

def plotSimulationComparison(dataframes, par, titles, rows, cols, aggregate = True, h_space = 0.55, w_space = 0.55):
    """
    Plots selected function applied to selected parameter over epoch for multiple simulations.

    Keyword arguments:
    dataframes  -- the data frames on which to base the comparisons
    par         -- the parameter for which the function should be calculated
    titles         -- the algorithm that was used as recommendation 
    rows        -- rows of sub plot figure
    cols        -- cols of sub plot figure
    aggregate   -- which function to calculate
    """
    if rows*cols != len(dataframes):
        raise Exception("The dimensions of the sub-plots must at least match the number of dataframes to compare")

    if len(dataframes) != len(titles):
        raise Exception("For each dataframe a title needs to be provided.")

    row = 0
    col = 0
    fig, axes = plt.subplots(nrows=rows, ncols=cols, sharey=True)
    plt.subplots_adjust(
            hspace=h_space,
            wspace=w_space
        )
    
    for frame, title in zip(dataframes, titles):

        if aggregate:
            dt = frame.groupby(["epoch"]).agg(
                aggPar = pd.NamedAgg(column=par, aggfunc = "mean")

                                                )
            axes[row, col].plot(range(len(dt)),dt, color="black")

        else:
            nodeIDs = set(frame["nodeID"])
            for ID in nodeIDs:
                dt_individual = frame[par][frame["nodeID"] == ID]
                axes[row, col].plot(range(len(dt_individual)), dt_individual, color = "black")
        
        axes[row, col].set_title(title)

        col += 1
        if col > (cols - 1):
            col = 0
            row += 1

    plt.savefig("DMAS/data_processing/img/simulation_comparisons/comparison_" + par + ".png")
    plt.close()

def plotHistogramComparison(dataframes, timepoints, par, titles, rows, cols, h_space = 0.55, w_space = 0.55):
    """
    Plots histogram for distribution of a selected parameter for
    selection of time points for different data sets.

    Keyword arguments:
    dataframes  -- the data frames from which to collect the data
    timePoints  -- the timepoints for which to create the histogram
    par         -- the parameter for which the histogram should be calculated
    titles      -- the titles for the dataframes
    rows        -- rows of sub plot figure
    cols        -- cols of sub plot figure
    """
    if rows*cols != len(dataframes)*len(timepoints):
            raise Exception("The dimensions of the sub-plots must at match the number of dataframes * number of timepoints to compare.")

    if len(dataframes) != len(titles):
        raise Exception("For each dataframe a title needs to be provided.")

    row = 0
    col = 0
    fig, axes = plt.subplots(nrows=rows, ncols=cols, sharey=True)
    plt.subplots_adjust(
            hspace=h_space,
            wspace=w_space
        )

    for frame, title in zip(dataframes, titles):

        for tp in timepoints:
            dt = frame[par][frame["epoch"] == tp]
            axes[row, col].hist(dt)
            axes[row, col].set_title(title + " Epoch: " + str(tp))
            col += 1
            if col > (cols - 1):
                col = 0
                row += 1

    plt.savefig("DMAS/data_processing/img/simulation_comparisons/hist_comparison_" + par + ".png")
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
    
    # Comparisons across dataframes:
    plotHistogramComparison([dataFrameNeutralize,
                             dataFramePolarize,
                             dataFrameRandom],
                             [1,10,25,50,100],
                             "belief",
                             ["N", "P", "R"],3,5)