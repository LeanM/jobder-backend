package com.jobder.app.search.services;

import com.jobder.app.authentication.dto.userdtos.WorkerDTO;
import com.jobder.app.authentication.exceptions.InvalidClientException;
import com.jobder.app.authentication.models.users.SearchParameters;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.matching.services.MatchingService;
import com.jobder.app.search.dto.RequestClientSearchInfo;
import com.jobder.app.search.dto.WorkerSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerSearchService {

    private final UserRepository userRepository;
    private final MatchingService matchingService;
    private final int defaultMinimumDistanceInKm = 50;
    private final int maxPageWorkers = 10;
    private final double EarthRadius = 6371000;
    private final double radians = 3.14159/180;

    public List<WorkerSearchResponse> searchWorkers(RequestClientSearchInfo clientSearchInfo) throws InvalidClientException {
        updateUserSearchParameters(clientSearchInfo);

        int pageNumber = 0;
        PageRequest pageRequest = PageRequest.of(pageNumber,maxPageWorkers);

        List<WorkerSearchResponse> bestAverageRatingWorkersForUserLocation;
        List<WorkerSearchResponse> workersFinalResult = new LinkedList<>();

        User searchingClient = userRepository.findById(clientSearchInfo.getId()).orElseThrow(() -> new InvalidClientException("No Client with that ID"));

        if(searchingClient.getLatitude() == null || searchingClient.getLongitude() == null)
            throw new InvalidClientException("Client has not a location setted");

        clientSearchInfo.setLongitude(searchingClient.getLongitude());
        clientSearchInfo.setLatitude(searchingClient.getLatitude());

        do {
            bestAverageRatingWorkersForUserLocation = obtainBestAverageRatingWorkersForUserInfo(clientSearchInfo, pageRequest);
            pageNumber += 1;
            pageRequest = PageRequest.of(pageNumber, maxPageWorkers);

            List<WorkerSearchResponse> availableWorkers = verifyAvailableWorkers(searchingClient, bestAverageRatingWorkersForUserLocation);

            for (int i = 0; (i < availableWorkers.size()); i++){
                workersFinalResult.add(availableWorkers.get(i));
            }
        } while (workersFinalResult.size() < maxPageWorkers && !bestAverageRatingWorkersForUserLocation.isEmpty());


        return workersFinalResult;
    }

    private void updateUserSearchParameters(RequestClientSearchInfo clientSearchInfo) throws InvalidClientException {
        if(userRepository.existsById(clientSearchInfo.getId())){
            User client = userRepository.findById(clientSearchInfo.getId()).orElseThrow(() -> new InvalidClientException("No Client with that ID"));
            SearchParameters updatedSearchParameters = new SearchParameters(clientSearchInfo.getClientProblemDescription(), clientSearchInfo.getWorkSpecialization(), clientSearchInfo.getAvailabilityStatus());
            client.setSearchParameters(updatedSearchParameters);
            userRepository.save(client);
        } else throw new InvalidClientException("No Client with that ID");
    }

    public List<WorkerSearchResponse> unloggedSearchWorkers(RequestClientSearchInfo clientSearchInfo) {
        int pageNumber = 0;
        PageRequest pageRequest = PageRequest.of(pageNumber,maxPageWorkers);

        List<WorkerSearchResponse> bestAverageRatingWorkersForUserLocation;
        List<WorkerSearchResponse> workersFinalResult = new LinkedList<>();

        do {
            bestAverageRatingWorkersForUserLocation = obtainBestAverageRatingWorkersForUserInfo(clientSearchInfo, pageRequest);
            pageNumber += 1;
            pageRequest = PageRequest.of(pageNumber, maxPageWorkers);

            for (int i = 0; (i < bestAverageRatingWorkersForUserLocation.size()); i++){
                workersFinalResult.add(bestAverageRatingWorkersForUserLocation.get(i));
            }
        } while (workersFinalResult.size() < maxPageWorkers && !bestAverageRatingWorkersForUserLocation.isEmpty());


        return workersFinalResult;
    }

    private List<WorkerSearchResponse> verifyAvailableWorkers(User searchingClient, List<WorkerSearchResponse> workersToVerify){
       return matchingService.validateWorkers(searchingClient, workersToVerify);
    }

    private int defineMinimumDistanceInKm(RequestClientSearchInfo clientSearchInfo){
        int searchMinimumDistanceInKm = defaultMinimumDistanceInKm;

        if(clientSearchInfo.getMinimumDistanceInKm() != null && clientSearchInfo.getMinimumDistanceInKm() > 0)
            searchMinimumDistanceInKm = clientSearchInfo.getMinimumDistanceInKm();

        return searchMinimumDistanceInKm;
    }

    private List<WorkerSearchResponse> obtainBestAverageRatingWorkersForUserInfo(RequestClientSearchInfo clientSearchInfo, PageRequest pageRequest){
        List<User> toReturn;

        if(clientSearchInfo.getWorkSpecialization() == "" || clientSearchInfo.getWorkSpecialization() == null){
            toReturn = clientSearchInfo.getAvailabilityStatus().name().equals("AVAILABLE") ? userRepository.findAvailableWorkers(pageRequest) : userRepository.findWorkers(pageRequest);
        }
        else
            toReturn = clientSearchInfo.getAvailabilityStatus().name().equals("AVAILABLE") ? userRepository.findAvailableWorkersByProfession(clientSearchInfo.getWorkSpecialization(), pageRequest) : userRepository.findWorkersByProfession(clientSearchInfo.getWorkSpecialization(), pageRequest);

        return filterCloseWorkers(clientSearchInfo, toReturn);
    }

    private List<WorkerSearchResponse> filterCloseWorkers(RequestClientSearchInfo clientSearchInfo, List<User> workers) {
        List<WorkerSearchResponse> filteredWorkers = new LinkedList<>();
        for (User worker : workers){
            Double distanceBetweenWorkerAndClient = distanceOfWorkerToClientInKm(worker, clientSearchInfo);
            if(distanceBetweenWorkerAndClient < defaultMinimumDistanceInKm){
                WorkerSearchResponse workerSearchResponse = new WorkerSearchResponse();
                workerSearchResponse.setUser(worker.toWorker());
                workerSearchResponse.setDistanceInKm(distanceBetweenWorkerAndClient);
                filteredWorkers.add(workerSearchResponse);
            }
        }

        return filteredWorkers;
    }


    private double distanceOfWorkerToClientInKm(User worker, RequestClientSearchInfo clientSearchInfo){
        double lngDelta = Math.abs(clientSearchInfo.getLongitude() - worker.getLongitude());
        if (lngDelta > 180)
            lngDelta = 360 - lngDelta;
        double p1 = lngDelta * Math.cos(0.5 * radians * (clientSearchInfo.getLatitude() + worker.getLatitude()));
        double p2 = (clientSearchInfo.getLatitude() - worker.getLatitude());
        return Math.floor( ((EarthRadius * radians * Math.sqrt( p1 * p1 + p2 * p2)) / 1000) * 100 ) / 100;
    }

}
