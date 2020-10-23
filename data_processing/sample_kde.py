import pandas as pd
from scipy.stats import gaussian_kde as kde
import statsmodels.api as sm
import numpy as np
import random as rd
from os import path


class Sampler():

    def __init__(self, n, method = "normal_reference"):

        """
        Initializes sampler class.

        Keyword arguments:

        n      -- the number of samples to draw from KDE of PDF (Probability Density Function)
        method -- method used to estimate bandwith (default normal_reference)

        """

        self.n = n
        self.method = method
        self.data = None
        self.sample = pd.DataFrame(columns=["neuroticism", "extraversion", "openness"])
        ### Used for fallback ###
        self.means = [0.44, 0.55, 0.67]
        self.cov = [[0.0390052298, -0.013795166, -0.0004315343],
                    [-0.0137951665,  0.022726950,  0.0037587265],
                    [-0.0004315343,  0.003758727,  0.0169329420]]


    def kernelSampling(self):

        """
        Uses mutlivariate KDE for bandwidth estimation and then samples from
        multivariate normal kernels for the re-sampling process. Enforces strict
        limit between 0 and 1 to match belief system.
        
        See also for reference:
        https://github.com/scipy/scipy/blob/master/scipy/stats/kde.py
        https://www.statsmodels.org/stable/generated/statsmodels.nonparametric.kernel_density.KDEMultivariate.html
        https://stackoverflow.com/questions/46488223/how-to-draw-samples-with-kernel-density-estimation
        https://stackoverflow.com/questions/50520535/resampling-a-kde-kernel-density-estimation-in-statsmodels

        """
        # Multivariate Kernel density estimate
        KDE = sm.nonparametric.KDEMultivariate(data=self.data,
                                               var_type="ccc",
                                               bw=self.method)
        
        # use bandwidth estimates for covariance structure
        cov = np.diag(KDE.bw)**2
        index = 1
        # random indices from sample
        randomSelection = np.random.randint(0,len(self.data)-1,n) 
        # sampling from multivariate normal kernel
        kernelSample = np.random.multivariate_normal([0,0,0], cov, size=self.n) 

        for rs,ks in zip(randomSelection,kernelSample):
            newSample = []
            for var in self.data.loc[rs,:]+ks:
                if var < 0:
                    newSample.append(0)
                elif var > 1:
                    newSample.append(1)
                else:
                    newSample.append(var)
            self.sample.loc[index] = newSample
            index += 1

    def mvnormalSampling(self):

        """
        Approximates the distributions by sampling from a multivariate normal
        distribution. This is the default fallback method in case no dataset
        is provided.
        """

        # Simply draw n samples from multivariate normal
        # using original means and covariance structure.
        samples = np.random.multivariate_normal(self.means, self.cov, size=self.n)
        index = 1
        for s in samples:
            newSample = []
            for var in s:
                if var < 0:
                    newSample.append(0)
                elif var > 1:
                    newSample.append(1)
                else:
                    newSample.append(var)
            self.sample.loc[index] = newSample
            index += 1
        
    def fit(self, pathToData = None, sep=",", header=0):

        """
        Fit allows to pass path to datafile, otherwise falls back to
        normal approximation method.

        Keyword arguments:

        pathToData -- the path to the data file
        sep        -- seperator to use when reading in csv (default ",")
        header     -- row from which to read column names (default 0)
        """

        if type(pathToData) == str:
            if path.exists(pathToData):
                self.data = pd.read_csv(pathToData, sep=sep, header=header)
                # Trim dataset to include only relevant columns in case of for example rownames
                self.data = self.data[["neuroticism", "extraversion", "openness"]]
                self.kernelSampling()
            else:
                raise FileNotFoundError("Please specify a valid path to a file.")
        else:
            self.mvnormalSampling()
    
    def export(self, pathToOutput, sep=",", header=True):

        """
        Export data to csv for use in model.

        Keyword arguments:

        pathToOutput -- the path to which to write the results
        sep          -- seperator to use when writing to csv csv (default ",")
        header       -- whether or not to include a header (default True)
        """

        self.sample.to_csv(pathToOutput, sep=sep, header=header, index=False)
    


if __name__ == "__main__":
    n = 1000
    sampler = Sampler(n=n)
    sampler.fit("DMAS/data_processing/data_agents/sampled_data_input.csv")
    sampler.export("DMAS/data_processing/data_agents/{}.csvagents".format(n))
