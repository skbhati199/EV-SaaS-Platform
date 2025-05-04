'use client';

import React, { useEffect, useRef } from 'react';

interface GrafanaEmbedProps {
  dashboardUrl: string;
  title?: string;
  height?: string;
  width?: string;
}

/**
 * Component to embed Grafana dashboards in the admin portal
 */
const GrafanaEmbed: React.FC<GrafanaEmbedProps> = ({
  dashboardUrl,
  title,
  height = '600px',
  width = '100%',
}) => {
  const iframeRef = useRef<HTMLIFrameElement>(null);

  useEffect(() => {
    // Handle iframe resize or other post-load operations if needed
    if (iframeRef.current) {
      // You can add additional iframe handling here
    }
  }, [dashboardUrl]);

  const baseUrl = process.env.NEXT_PUBLIC_GRAFANA_URL || 'http://localhost:3000';
  const fullUrl = `${baseUrl}${dashboardUrl}`;

  return (
    <div className="grafana-embed-container">
      {title && <h2 className="text-xl font-semibold mb-4">{title}</h2>}
      <div className="grafana-iframe-container bg-white rounded-lg shadow-md overflow-hidden">
        <iframe
          ref={iframeRef}
          src={fullUrl}
          width={width}
          height={height}
          frameBorder="0"
          title={title || 'Grafana Dashboard'}
          className="grafana-iframe"
          allowFullScreen
        />
      </div>
    </div>
  );
};

export default GrafanaEmbed; 