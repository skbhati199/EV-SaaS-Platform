'use client';

import { useState } from 'react';
import Link from 'next/link';

export default function DashboardPage() {
  const [stations, setStations] = useState([
    { id: 'station-001', name: 'EV Station Alpha', location: 'Downtown', status: 'Online', connectors: 4 },
    { id: 'station-002', name: 'EV Station Beta', location: 'Uptown', status: 'Offline', connectors: 2 },
    { id: 'station-003', name: 'EV Station Gamma', location: 'Midtown', status: 'Online', connectors: 6 },
  ]);
  
  const [showAddModal, setShowAddModal] = useState(false);
  const [newStation, setNewStation] = useState({
    name: '',
    location: '',
    connectors: 2
  });

  const handleAddStation = (e: React.FormEvent) => {
    e.preventDefault();
    const newId = `station-${Math.floor(Math.random() * 1000).toString().padStart(3, '0')}`;
    
    setStations([...stations, {
      id: newId,
      name: newStation.name,
      location: newStation.location,
      status: 'Offline',
      connectors: newStation.connectors
    }]);
    
    setNewStation({ name: '', location: '', connectors: 2 });
    setShowAddModal(false);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8 flex justify-between items-center">
          <h1 className="text-3xl font-bold tracking-tight text-gray-900">EV SaaS Platform</h1>
          <div className="flex items-center space-x-4">
            <span className="text-gray-700">Admin User</span>
            <button className="text-sm text-red-600 hover:text-red-800">Logout</button>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main>
        <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-900">EVSE Stations</h2>
            <button
              onClick={() => setShowAddModal(true)}
              className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              Add New Station
            </button>
          </div>

          {/* Stations Table */}
          <div className="bg-white shadow overflow-hidden sm:rounded-lg">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Location</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Connectors</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {stations.map((station) => (
                  <tr key={station.id}>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{station.id}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{station.name}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{station.location}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        station.status === 'Online' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {station.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{station.connectors}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <Link href={`/dashboard/stations/${station.id}`} className="text-blue-600 hover:text-blue-900 mr-4">View</Link>
                      <button className="text-red-600 hover:text-red-900">Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </main>

      {/* Add Station Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg max-w-md w-full p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Add New EVSE Station</h3>
            
            <form onSubmit={handleAddStation}>
              <div className="mb-4">
                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">Station Name</label>
                <input
                  type="text"
                  id="name"
                  value={newStation.name}
                  onChange={(e) => setNewStation({...newStation, name: e.target.value})}
                  className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              
              <div className="mb-4">
                <label htmlFor="location" className="block text-sm font-medium text-gray-700 mb-1">Location</label>
                <input
                  type="text"
                  id="location"
                  value={newStation.location}
                  onChange={(e) => setNewStation({...newStation, location: e.target.value})}
                  className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              
              <div className="mb-4">
                <label htmlFor="connectors" className="block text-sm font-medium text-gray-700 mb-1">Number of Connectors</label>
                <select
                  id="connectors"
                  value={newStation.connectors}
                  onChange={(e) => setNewStation({...newStation, connectors: parseInt(e.target.value)})}
                  className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                  {[1, 2, 3, 4, 5, 6, 8, 10].map(num => (
                    <option key={num} value={num}>{num}</option>
                  ))}
                </select>
              </div>
              
              <div className="flex justify-end space-x-3 mt-6">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  Add Station
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
