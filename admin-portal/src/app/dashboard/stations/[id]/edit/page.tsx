"use client";

import StationForm from "@/app/components/StationForm";

interface EditStationPageProps {
  params: {
    id: string;
  };
}

export default function EditStationPage({ params }: EditStationPageProps) {
  return <StationForm stationId={params.id} isEditing={true} />;
} 