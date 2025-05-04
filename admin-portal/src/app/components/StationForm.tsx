"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { 
  Form, 
  FormControl, 
  FormField, 
  FormItem, 
  FormLabel, 
  FormMessage 
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { AlertCircle, ArrowLeft, Loader2, Plus, Trash } from "lucide-react";
import { stationService, Station } from "@/app/services/stationService";

// Define the form schema
const formSchema = z.object({
  name: z.string().min(2, { message: "Name must be at least 2 characters" }),
  vendor: z.string().min(2, { message: "Vendor name is required" }),
  model: z.string().min(2, { message: "Model is required" }),
  serialNumber: z.string().optional(),
  location: z.object({
    address: z.string().min(5, { message: "Address is required" }),
    city: z.string().min(2, { message: "City is required" }),
    zipCode: z.string().min(2, { message: "Zip code is required" }),
    country: z.string().min(2, { message: "Country is required" }),
    latitude: z.coerce.number(),
    longitude: z.coerce.number(),
  }),
  connectors: z.array(z.object({
    id: z.string().optional(),
    type: z.string().min(2, { message: "Connector type is required" }),
    maxPower: z.coerce.number(),
    status: z.string().optional(),
  })).min(1, { message: "At least one connector is required" }),
});

type FormData = z.infer<typeof formSchema>;

interface StationFormProps {
  stationId?: string;
  isEditing?: boolean;
}

export default function StationForm({ stationId, isEditing = false }: StationFormProps) {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(isEditing);
  const [error, setError] = useState<string | null>(null);

  // Initialize the form
  const form = useForm<FormData>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      vendor: "",
      model: "",
      serialNumber: "",
      location: {
        address: "",
        city: "",
        zipCode: "",
        country: "",
        latitude: 0,
        longitude: 0,
      },
      connectors: [
        {
          type: "Type 2",
          maxPower: 22,
          status: "AVAILABLE",
        },
      ],
    },
  });

  // Fetch station data if editing
  useEffect(() => {
    const fetchStationData = async () => {
      if (isEditing && stationId) {
        setFetchLoading(true);
        try {
          const stationData = await stationService.getStation(stationId);
          form.reset({
            name: stationData.name,
            vendor: stationData.vendor,
            model: stationData.model,
            serialNumber: stationData.serialNumber || "",
            location: {
              address: stationData.location.address,
              city: stationData.location.city,
              zipCode: stationData.location.zipCode,
              country: stationData.location.country,
              latitude: stationData.location.latitude,
              longitude: stationData.location.longitude,
            },
            connectors: stationData.connectors.map(connector => ({
              id: connector.id,
              type: connector.type,
              maxPower: connector.maxPower,
              status: connector.status,
            })),
          });
        } catch (err) {
          console.error("Error fetching station data:", err);
          setError("Failed to load station data. Please try again.");
        } finally {
          setFetchLoading(false);
        }
      }
    };

    fetchStationData();
  }, [isEditing, stationId, form]);

  const onSubmit = async (data: FormData) => {
    setLoading(true);
    setError(null);
    try {
      // Process data to match API expectations
      const processedData: Partial<Station> = {
        ...data,
        connectors: data.connectors.map(connector => ({
          ...connector,
          id: connector.id || '', // Ensure id is a string, not undefined
          maxPower: Number(connector.maxPower),
          status: (connector.status || 'AVAILABLE') as 'AVAILABLE' | 'OCCUPIED' | 'RESERVED' | 'UNAVAILABLE' | 'FAULTED'
        }))
      };
      
      if (isEditing && stationId) {
        await stationService.updateStation(stationId, processedData);
        router.push(`/dashboard/stations/${stationId}`);
      } else {
        const newStation = await stationService.createStation(processedData);
        router.push(`/dashboard/stations/${newStation.id}`);
      }
    } catch (err) {
      console.error("Error saving station:", err);
      setError("Failed to save station. Please try again.");
      setLoading(false);
    }
  };

  const addConnector = () => {
    const currentConnectors = form.getValues("connectors");
    form.setValue("connectors", [
      ...currentConnectors,
      { type: "Type 2", maxPower: 22, status: "AVAILABLE" },
    ]);
  };

  const removeConnector = (index: number) => {
    const currentConnectors = form.getValues("connectors");
    if (currentConnectors.length > 1) {
      form.setValue(
        "connectors",
        currentConnectors.filter((_, i) => i !== index)
      );
    }
  };

  if (fetchLoading) {
    return (
      <div className="flex items-center justify-center p-12">
        <div className="flex flex-col items-center">
          <Loader2 className="h-12 w-12 animate-spin text-accent" />
          <p className="mt-4 text-muted-foreground">Loading station data...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <Button
            variant="ghost"
            size="sm"
            className="gap-1 mb-1"
            onClick={() => router.push("/dashboard/stations")}
          >
            <ArrowLeft className="h-4 w-4" />
            <span>Back to Stations</span>
          </Button>
          <h1 className="text-3xl font-bold tracking-tight">
            {isEditing ? "Edit Station" : "Add New Station"}
          </h1>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 dark:bg-red-900/30 border-l-4 border-red-500 p-4 text-red-700 dark:text-red-400 flex items-start gap-3">
          <AlertCircle className="h-5 w-5 mt-0.5" />
          <p>{error}</p>
        </div>
      )}

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <FormField
                  control={form.control}
                  name="name"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Station Name*</FormLabel>
                      <FormControl>
                        <Input placeholder="Enter station name" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="serialNumber"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Serial Number</FormLabel>
                      <FormControl>
                        <Input placeholder="Enter serial number" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <FormField
                  control={form.control}
                  name="vendor"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Vendor*</FormLabel>
                      <FormControl>
                        <Input placeholder="E.g., ChargePoint, ABB, Tesla" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="model"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Model*</FormLabel>
                      <FormControl>
                        <Input placeholder="E.g., CT4000, Terra 54" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Location Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <FormField
                control={form.control}
                name="location.address"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Address*</FormLabel>
                    <FormControl>
                      <Input placeholder="Street address" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <FormField
                  control={form.control}
                  name="location.city"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>City*</FormLabel>
                      <FormControl>
                        <Input placeholder="City" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="location.zipCode"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Zip Code*</FormLabel>
                      <FormControl>
                        <Input placeholder="Zip Code" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="location.country"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Country*</FormLabel>
                      <FormControl>
                        <Input placeholder="Country" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <FormField
                  control={form.control}
                  name="location.latitude"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Latitude*</FormLabel>
                      <FormControl>
                        <Input
                          type="number"
                          step="any"
                          placeholder="Latitude"
                          {...field}
                          onChange={(e) => field.onChange(parseFloat(e.target.value) || 0)}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="location.longitude"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Longitude*</FormLabel>
                      <FormControl>
                        <Input
                          type="number"
                          step="any"
                          placeholder="Longitude"
                          {...field}
                          onChange={(e) => field.onChange(parseFloat(e.target.value) || 0)}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Connectors</CardTitle>
              <Button type="button" variant="outline" size="sm" onClick={addConnector}>
                <Plus className="h-4 w-4 mr-2" /> Add Connector
              </Button>
            </CardHeader>
            <CardContent>
              {form.watch("connectors").map((_, index) => (
                <div key={index} className="mb-6 last:mb-0">
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="font-medium">Connector {index + 1}</h3>
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      className="h-8 w-8 p-0 text-red-500"
                      onClick={() => removeConnector(index)}
                      disabled={form.watch("connectors").length <= 1}
                    >
                      <Trash className="h-4 w-4" />
                    </Button>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <FormField
                      control={form.control}
                      name={`connectors.${index}.type`}
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Connector Type*</FormLabel>
                          <FormControl>
                            <Input
                              placeholder="E.g., Type 2, CCS, CHAdeMO"
                              {...field}
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name={`connectors.${index}.maxPower`}
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Max Power (kW)*</FormLabel>
                          <FormControl>
                            <Input
                              type="number"
                              placeholder="Max power in kW"
                              {...field}
                              onChange={(e) =>
                                field.onChange(parseFloat(e.target.value) || 0)
                              }
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                  {index < form.watch("connectors").length - 1 && (
                    <Separator className="mt-4" />
                  )}
                </div>
              ))}
            </CardContent>
            <CardFooter className="flex justify-between">
              <Button
                type="button"
                variant="outline"
                onClick={() => router.push("/dashboard/stations")}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={loading} className="bg-accent hover:bg-accent/90">
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    {isEditing ? "Saving..." : "Creating..."}
                  </>
                ) : (
                  <>{isEditing ? "Save Changes" : "Create Station"}</>
                )}
              </Button>
            </CardFooter>
          </Card>
        </form>
      </Form>
    </div>
  );
} 