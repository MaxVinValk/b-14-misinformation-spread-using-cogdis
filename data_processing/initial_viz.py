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



if __name__ == "__main__":
    # Read data into pd dataframe
    dataFrameRandom = pd.read_csv("https://onedrive.live.com/download?cid=E556A05E08C89AB7&resid=E556A05E08C89AB7%211047920&authkey=ABgnRxegPx4RAMY", sep=",", header=0)
    dataFramePolarize = pd.read_csv("https://onedrive.live.com/download?cid=E556A05E08C89AB7&resid=E556A05E08C89AB7%211047921&authkey=APTLP1ZYXNT7IxM", sep=",", header=0)
    
    
    # Plot trajectory of individual beliefs over epochs to show convergence
    plotBeliefConvergence(dataFrameRandom, "random")
    plotBeliefConvergence(dataFramePolarize, "polarize")

    # List all stats you want to average over for both the random and polarized dataset
    avgPars = ["dissonance", "numNeighbours", "avgNeighbourBelief",
               "numConfidants","avgConfidantBelief","numberOfContacts",
               "numberOfConflicts"]

    # Iterate over parameters and save the figures in the respective folders.
    for par in avgPars:
        print("Processing: " + par + " (No worries I am still doing my job :) )")
        plotAverageOverEpochs(dataFrameRandom, par, "random")
        plotAverageOverEpochs(dataFramePolarize, par, "polarize")

    