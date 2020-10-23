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
    fig, axes = plt.subplots(nrows=rows, ncols=cols, sharey=True, sharex=True)
    plt.subplots_adjust(
            hspace=h_space,
            wspace=w_space
        )

    for tp in timepoints:
        dt = dataframe[par][dataframe["epoch"] == tp]
        axes[row, col].hist(dt)
        axes[row, col].set_title("Epoch: " + str(tp), fontsize=5)
        col += 1
        if col > (cols - 1):
            col = 0
            row += 1

    plt.savefig("DMAS/data_processing/img/" + alg + "/histogram_" + par + ".png")
    plt.close()

def plotSimulationComparison(dataframes, par, alg, titles, rows=0, cols=0, aggregate = True, w_space = 0.55, h_space=0.55):
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
    if cols != len(dataframes) and rows != len(dataframes):
        raise Exception("The dimensions of the sub-plots must match the number of dataframes to compare")

    if len(dataframes) != len(titles):
        raise Exception("For each dataframe a title needs to be provided.")

    if rows > 0 and cols > 0:
        raise Exception("Please only define rows OR columns, not both.")


    index = 0
    if rows == 0:
        fig, axes = plt.subplots(ncols=cols, sharey=True, sharex=True)
    else:
        fig, axes = plt.subplots(nrows=rows, sharey=True, sharex=True)
    
    plt.subplots_adjust(
            hspace=h_space,
            wspace=w_space
        )
    
    for frame, title in zip(dataframes, titles):

        if aggregate:
            dt = frame.groupby(["epoch"]).agg(
                aggPar = pd.NamedAgg(column=par, aggfunc = "mean")

                                                )
            axes[index].plot(range(len(dt)),dt, color="black")

        else:
            nodeIDs = set(frame["nodeID"])
            for ID in nodeIDs:
                dt_individual = frame[par][frame["nodeID"] == ID]
                axes[index].plot(range(len(dt_individual)), dt_individual, color = "black")
        
        axes[index].set_title(title, fontsize=10)

        index += 1

    plt.savefig("DMAS/data_processing/img/simulation_comparisons/comparison_" + alg + "_" + par + ".png")
    plt.close()

def plotHistogramComparison(dataframes, timepoints, par, alg, titles, rows, cols, h_space = 0.55, w_space = 0.55):
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
    fig, axes = plt.subplots(nrows=rows, ncols=cols, sharey=True, sharex=True)
    plt.subplots_adjust(
            hspace=h_space,
            wspace=w_space
        )

    for frame, title in zip(dataframes, titles):

        for tp in timepoints:
            dt = frame[par][frame["epoch"] == tp]
            axes[row, col].hist(dt)
            axes[row, col].set_title(title + " Epoch: " + str(tp), fontsize=5)
            col += 1
            if col > (cols - 1):
                col = 0
                row += 1

    plt.savefig("DMAS/data_processing/img/simulation_comparisons/hist_comparison_" + alg + "_" + par + ".png")
    plt.close()
    
if __name__ == "__main__":
    # Read data into pd dataframe

    # Old dataframes that were available online do not work at the moment since they are being replaced.

    #dataFrameRandom = pd.read_csv("https://onedrive.live.com/download?cid=E556A05E08C89AB7&resid=E556A05E08C89AB7%211047920&authkey=ABgnRxegPx4RAMY", sep=",", header=0)
    #dataFramePolarize = pd.read_csv("https://onedrive.live.com/download?cid=E556A05E08C89AB7&resid=E556A05E08C89AB7%211047921&authkey=APTLP1ZYXNT7IxM", sep=",", header=0)

    # Dataframes to test impact of openness and dissonance ratio weights.

    dataPolarizeOpenness005 = pd.read_csv("data_out/polarize_100_0.05_0/data.csv", sep=",", header=0)
    dataPolarizeOpenness010 = pd.read_csv("data_out/polarize_100_0.1_0/data.csv", sep=",", header=0)
    dataPolarizeOpenness015 = pd.read_csv("data_out/polarize_100_0.15_0/data.csv", sep=",", header=0)
    dataPolarizeOpenness020 = pd.read_csv("data_out/polarize_100_0.2_0/data.csv", sep=",", header=0)
    dataPolarizeOpenness025 = pd.read_csv("data_out/polarize_100_0.25_0/data.csv", sep=",", header=0)

    opennessFrames = [dataPolarizeOpenness005,
                     dataPolarizeOpenness010,
                     dataPolarizeOpenness015,
                     dataPolarizeOpenness020,
                     dataPolarizeOpenness025]

    dataPolarizeDissonanceWeight1 = pd.read_csv("data_out/polarize_100_0.25_1/data.csv", sep=",", header=0)
    dataPolarizeDissonanceWeight095 = pd.read_csv("data_out/polarize_100_0.25_0.95/data.csv", sep=",", header=0)
    dataPolarizeDissonanceWeight090 = pd.read_csv("data_out/polarize_100_0.25_0.9/data.csv", sep=",", header=0)
    dataPolarizeDissonanceWeight085 = pd.read_csv("data_out/polarize_100_0.25_0.85/data.csv", sep=",", header=0)
    dataPolarizeDissonanceWeight080 = pd.read_csv("data_out/polarize_100_0.25_0.8/data.csv", sep=",", header=0)

    dissonanceFrames = [dataPolarizeDissonanceWeight1,
                        dataPolarizeDissonanceWeight095,
                        dataPolarizeDissonanceWeight090,
                        dataPolarizeDissonanceWeight085,
                        dataPolarizeDissonanceWeight080]

    # Dataframes to compare impact of strategies

    dataRandomize = pd.read_csv("data_out/randomize_100_0.25_0.85/data.csv", sep=",", header=0)
    dataNeutralize = pd.read_csv("data_out/neutralize_100_0.25_0.85/data.csv", sep=",", header=0)

    strategyFrames = [dataPolarizeDissonanceWeight085,
                      dataRandomize,
                      dataNeutralize]

    # Parameters for which to compare averages over epochs

    avgPars = ["dissonance", "numNeighbours",
               "numConfidants"]

    # Timepoints for histograms
    tp = [1,10,15,20,1000]

    # Comparison for openness changes

    plotHistogramComparison(opennessFrames,
                             tp,
                             "belief",
                             "varying_openness",
                             ["O: 0.05", "O: 0.1", "O: 0.15",
                              "O: 0.20", "O: 0.25"],
                              len(opennessFrames),len(tp),
                              h_space=0.5,w_space=0.3)

    plotSimulationComparison(opennessFrames,
                             "belief",
                             "varying_openness",
                             ["O: 0.05", "O: 0.1", "O: 0.15",
                              "O: 0.20", "O: 0.25"],
                              rows=len(opennessFrames),
                              h_space=0.7,aggregate=False)
    
    for par in avgPars:
        plotSimulationComparison(opennessFrames,
                                 par,
                                 "varying_openness",
                                 ["O: 0.05", "O: 0.1", "O: 0.15",
                                 "O: 0.20", "O: 0.25"],
                                 rows=len(opennessFrames),
                                 h_space=0.7)

    # Comparison for dissonance weight impact changes

    plotHistogramComparison(dissonanceFrames,
                             tp,
                             "belief",
                             "varying_dissonance",
                             ["D: 1.0", "D: 0.95", "D: 0.9",
                              "D: 0.85", "D: 0.8"],
                              len(dissonanceFrames),len(tp),
                              h_space=0.5,w_space=0.3)
    
    plotSimulationComparison(dissonanceFrames,
                             "belief",
                             "varying_dissonance",
                             ["D: 1.0", "D: 0.95", "D: 0.9",
                              "D: 0.85", "D: 0.8"],
                              rows=len(dissonanceFrames),
                              h_space=0.7, aggregate=False)

    for par in avgPars:
        plotSimulationComparison(dissonanceFrames,
                                 par,
                                 "varying_dissonance",
                                 ["D: 1.0", "D: 0.95", "D: 0.9",
                                 "D: 0.85", "D: 0.8"],
                                 rows=len(dissonanceFrames),
                                 h_space=0.7)

    # Evaluating impact of strategy change

    plotHistogramComparison(strategyFrames,
                             tp,
                             "belief",
                             "strategy_comparisons",
                             ["Pol. ", "Ran. ", "Neut. "],
                             len(strategyFrames),len(tp),
                             h_space=0.5,w_space=0.3)
    
    plotSimulationComparison(strategyFrames,
                             "belief",
                             "strategy_comparisons",
                             ["Pol. ", "Ran. ", "Neut. "],
                             rows=len(strategyFrames),
                              h_space=0.65, aggregate=False)

    for par in avgPars:
        plotSimulationComparison(strategyFrames,
                                 par,
                                 "strategy_comparisons",
                                 ["Pol. ", "Ran. ", "Neut. "],
                                 rows=len(strategyFrames),
                                 h_space=0.65)
