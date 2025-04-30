'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';

interface StationDetailProps {
  params: {
    id: string;
  };
}

export default function StationDetailPage({ params }: StationDetailProps) {
  const { id } = params;
  const [station, setStation] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    // In a real application, this would be an API call
    // Simulating API call with setTimeout
    setTimeout(() => {
      // Mock data based on the ID
      if (id === 'station-001') {
        setStation({
          id: 'station-001',
          name: 'EV Station Alpha',
          location: 'Downtown',
          status: 'Online',
          connectors: 4,
          model: 'ChargePoint CT4000',
          serialNumber: 'CP-00123456',
          lastConnection: '2023-06-15T10:30:00Z',
          firmwareVersion: '5.2.1',
          connectorDetails: [
            { id: 1, type: 'Type 2', status: 'Available', power: '22kW' },
            { id: 2, type: 'CCS', status: 'Charging', power: '50kW' },
            { id: 3, type: 'CHAdeMO', status: 'Available', power: '50kW' },
            { id: 4, type: 'Type 2', status: 'Offline', power: '22kW' },
          ]
        });
      } else if (id === 'station-002') {
        setStation({
          id: 'station-002',
          name: 'EV Station Beta',
          location: 'Uptown',
          status: 'Offline',
          connectors: 2,
          model: 'ABB Terra 54',
          serialNumber: 'ABB-00789012',
          lastConnection: '2023-06-10T08:15:00Z',
          firmwareVersion: '3.1.0',
          connectorDetails: [
            { id: 1, type: 'CCS', status: 'Offline', power: '50kW' },
            { id: 2, type: 'CHAdeMO', status: 'Offline', power: '50kW' },
          ]
        });
      } else if (id === 'station-003') {
        setStation({
          id: 'station-003',
          name: 'EV Station Gamma',
          location: 'Midtown',
          status: 'Online',
          connectors: 6,
          model: 'Tesla Supercharger V3',
          serialNumber: 'TSLA-00345678',
          lastConnection: '2023-06-16T14:45:00Z',
          firmwareVersion: '2023.12.1',
          connectorDetails: [
            { id: 1, type: 'Tesla', status: 'Available', power: '250kW' },
            { id: 2, type: 'Tesla', status: 'Charging', power: '250kW' },
            { id: 3, type: 'Tesla', status: 'Available', power: '250kW' },
            { id: 4, type: 'Tesla', status: 'Available', power: '250kW' },
            { id: 5, type: 'Tesla', status: 'Charging', power: '250kW' },
            { id: 6, type: 'Tesla', status: 'Available', power: '250kW' },
          ]
        });
      } else {
        // Generate a random station for any other ID
        setStation({
          id: id,
          name: `EV Station ${id.split('-')[1]}`,
          location: 'Unknown Location',
          status: Math.random() > 0.5 ? 'Online' : 'Offline',
          connectors: Math.floor(Math.random() * 6) + 1,
          model: 'Generic EVSE',
          serialNumber: `GEN-${Math.floor(Math.random() * 1000000)}`,
          lastConnection: new Date().toISOString(),
          firmwareVersion: '1.0.0',
          connectorDetails: Array(Math.floor(Math.random() * 6) + 1).fill(0).map((_, i) => ({
            id: i + 1,
            type: ['Type 2', 'CCS', 'CHAdeMO', 'Tesla'][Math.floor(Math.random() * 4)],
            status: ['Available', 'Charging', 'Offline'][Math.floor(Math.random() * 3)],
            power: ['22kW', '50kW', '150kW', '250kW'][Math.floor(Math.random() * 4)]
          }))
        });
      }
      setLoading(false);
    }, 1000);
  }, [id]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-700">Loading station details...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center text-red-600">
          <p>Error: {error}</p>
          <Link href="/dashboard" className="mt-4 inline-block text-blue-600 hover:underline">
            Return to Dashboard
          </Link>
        </div>
      </div>
    );
  }

  if (!station) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-700">Station not found</p>
          <Link href="/dashboard" className="mt-4 inline-block text-blue-600 hover:underline">
            Return to Dashboard
          </Link>
        </div>
      </div>
    );
  }

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
            <div>
              <Link href="/dashboard" className="text-blue-600 hover:underline mb-2 inline-block">
                &larr; Back to Dashboard
              </Link>
              <h2 className="text-2xl font-semibold text-gray-900">{station.name}</h2>
              <p className="text-gray-600">{station.location}</p>
            </div>
            <div className="flex space-x-3">
              <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
                Edit Station
              </button>
              <button className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500">
                {station.status === 'Online' ? 'Reboot Station' : 'Connect Station'}
              </button>
            </div>
          </div>

          {/* Station Details */}
          <div className="bg-white shadow overflow-hidden sm:rounded-lg mb-6">
            <div className="px-4 py-5 sm:px-6">
              <h3 className="text-lg leading-6 font-medium text-gray-900">Station Information</h3>
              <p className="mt-1 max-w-2xl text-sm text-gray-500">Details about the charging station.</p>
            </div>
            <div className="border-t border-gray-200">
              <dl>
                <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Station ID</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{station.id}</dd>
                </div>
                <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Status</dt>
                  <dd className="mt-1 text-sm sm:mt-0 sm:col-span-2">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                      station.status === 'Online' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    }`}>
                      {station.status}
                    </span>
                  </dd>
                </div>
                <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Model</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{station.model}</dd>
                </div>
                <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Serial Number</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{station.serialNumber}</dd>
                </div>
                <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Last Connection</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                    {new Date(station.lastConnection).toLocaleString()}
                  </dd>
                </div>
                <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Firmware Version</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{station.firmwareVersion}</dd>
                </div>
              </dl>
            </div>
          </div>

          {/* Connectors */}
          <div className="bg-white shadow overflow-hidden sm:rounded-lg">
            <div className="px-4 py-5 sm:px-6">
              <h3 className="text-lg leading-6 font-medium text-gray-900">Connectors</h3>
              <p className="mt-1 max-w-2xl text-sm text-gray-500">Information about the station's connectors.</p>
            </div>
            <div className="border-t border-gray-200">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                    <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                    <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                    <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Power</th>
                    <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {station.connectorDetails.map((connector: any) => (
                    <tr key={connector.id}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{connector.id}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{connector.type}</td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                          connector.status === 'Available' ? 'bg-green-100 text-green-800' : 
                          connector.status === 'Charging' ? 'bg-blue-100 text-blue-800' : 
                          'bg-red-100 text-red-800'
                        }`}>
                          {connector.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{connector.power}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button className="text-blue-600 hover:text-blue-900 mr-4">Reset</button>
                        <button className="text-red-600 hover:text-red-900">Disable</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
